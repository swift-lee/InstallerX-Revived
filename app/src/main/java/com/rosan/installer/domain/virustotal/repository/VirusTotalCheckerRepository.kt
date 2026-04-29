// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.domain.virustotal.repository

import com.rosan.installer.domain.virustotal.model.VirusTotalCheckResult

interface VirusTotalCheckerRepository {
    suspend fun checkSha256(
        sha256: String,
        apiKey: String,
        endpoint: String
    ): VirusTotalCheckResult
}
