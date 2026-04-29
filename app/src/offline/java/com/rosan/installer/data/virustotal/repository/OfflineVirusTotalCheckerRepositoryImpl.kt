// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.data.virustotal.repository

import com.rosan.installer.domain.virustotal.model.VirusTotalCheckResult
import com.rosan.installer.domain.virustotal.repository.VirusTotalCheckerRepository
import timber.log.Timber

class OfflineVirusTotalCheckerRepositoryImpl : VirusTotalCheckerRepository {
    override suspend fun checkSha256(
        sha256: String,
        apiKey: String,
        endpoint: String
    ): VirusTotalCheckResult {
        Timber.d("VirusTotal check disabled: Offline build")
        return VirusTotalCheckResult.ApiError(
            sha256 = sha256,
            message = "VirusTotal check is disabled in the offline build"
        )
    }
}
