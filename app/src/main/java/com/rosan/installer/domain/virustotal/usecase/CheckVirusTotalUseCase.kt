// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.domain.virustotal.usecase

import com.rosan.installer.domain.engine.model.DataEntity
import com.rosan.installer.domain.virustotal.model.VirusTotalCheckResult
import com.rosan.installer.domain.virustotal.repository.VirusTotalCheckerRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.security.MessageDigest

class CheckVirusTotalUseCase(
    private val repository: VirusTotalCheckerRepository
) {
    suspend operator fun invoke(
        data: DataEntity,
        apiKey: String,
        endpoint: String
    ): VirusTotalCheckResult {
        val sha256 = calculateSha256(data)
            ?: return VirusTotalCheckResult.ApiError(
                sha256 = "",
                message = "Unable to calculate APK SHA-256"
            )

        if (apiKey.isBlank()) {
            return VirusTotalCheckResult.ApiError(
                sha256 = sha256,
                message = "VirusTotal API key is not configured"
            )
        }

        return repository.checkSha256(
            sha256 = sha256,
            apiKey = apiKey.trim(),
            endpoint = normalizeEndpoint(endpoint)
        )
    }

    private suspend fun calculateSha256(data: DataEntity): String? = withContext(Dispatchers.IO) {
        val inputStream = data.getInputStreamWhileNotEmpty()
        if (inputStream == null) {
            Timber.w("Unable to calculate VirusTotal hash: input stream is unavailable")
            return@withContext null
        }

        runCatching {
            val digest = MessageDigest.getInstance("SHA-256")
            inputStream.use { stream ->
                val buffer = ByteArray(DEFAULT_BUFFER_SIZE)
                while (true) {
                    val read = stream.read(buffer)
                    if (read == -1) break
                    digest.update(buffer, 0, read)
                }
            }
            digest.digest().joinToString("") { "%02x".format(it) }
        }.onFailure {
            Timber.e(it, "Failed to calculate VirusTotal SHA-256")
        }.getOrNull()
    }

    private fun normalizeEndpoint(endpoint: String): String = endpoint
        .trim()
        .ifBlank { DEFAULT_ENDPOINT }
        .trimEnd('/')

    private companion object {
        const val DEFAULT_ENDPOINT = "https://www.virustotal.com/api/v3"
    }
}
