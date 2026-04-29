// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.data.virustotal.repository

import com.rosan.installer.data.virustotal.model.VirusTotalFileResponse
import com.rosan.installer.domain.virustotal.model.VirusTotalCheckResult
import com.rosan.installer.domain.virustotal.repository.VirusTotalCheckerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import timber.log.Timber
import java.io.IOException

class OnlineVirusTotalCheckerRepositoryImpl(
    private val client: OkHttpClient,
    private val json: Json
) : VirusTotalCheckerRepository {
    override suspend fun checkSha256(
        sha256: String,
        apiKey: String,
        endpoint: String
    ): VirusTotalCheckResult = withContext(Dispatchers.IO) {
        val detailUrl = buildDetailUrl(sha256)
        val request = Request.Builder()
            .url("$endpoint/files/$sha256")
            .header("x-apikey", apiKey)
            .header("Accept", "application/json")
            .build()

        try {
            client.newCall(request).execute().use { response ->
                val body = response.body.string()
                if (!response.isSuccessful) {
                    return@withContext VirusTotalCheckResult.ApiError(
                        sha256 = sha256,
                        message = body.ifBlank { response.message },
                        statusCode = response.code,
                        detailUrl = detailUrl
                    )
                }

                val fileResponse = runCatching {
                    json.decodeFromString<VirusTotalFileResponse>(body)
                }.getOrElse {
                    if (it is SerializationException) {
                        Timber.e(it, "Failed to parse VirusTotal response")
                    }
                    return@withContext VirusTotalCheckResult.ApiError(
                        sha256 = sha256,
                        message = "Unable to parse VirusTotal response",
                        statusCode = response.code,
                        detailUrl = detailUrl
                    )
                }

                val stats = fileResponse.data?.attributes?.last_analysis_stats
                    ?: return@withContext VirusTotalCheckResult.ApiError(
                        sha256 = sha256,
                        message = "VirusTotal response did not include analysis stats",
                        statusCode = response.code,
                        detailUrl = detailUrl
                    )
                if (stats.malicious > 0 || stats.suspicious > 0) {
                    VirusTotalCheckResult.Risky(
                        sha256 = sha256,
                        malicious = stats.malicious,
                        suspicious = stats.suspicious,
                        undetected = stats.undetected,
                        detailUrl = detailUrl
                    )
                } else {
                    VirusTotalCheckResult.Safe(
                        sha256 = sha256,
                        malicious = stats.malicious,
                        suspicious = stats.suspicious,
                        undetected = stats.undetected,
                        detailUrl = detailUrl
                    )
                }
            }
        } catch (e: IOException) {
            Timber.e(e, "VirusTotal network request failed")
            VirusTotalCheckResult.NetworkError(
                sha256 = sha256,
                message = e.message ?: "VirusTotal network request failed",
                detailUrl = detailUrl
            )
        } catch (e: IllegalArgumentException) {
            Timber.e(e, "Invalid VirusTotal endpoint")
            VirusTotalCheckResult.ApiError(
                sha256 = sha256,
                message = e.message ?: "Invalid VirusTotal endpoint",
                detailUrl = detailUrl
            )
        }
    }

    private fun buildDetailUrl(sha256: String): String = "https://www.virustotal.com/gui/file/$sha256/detection"
}
