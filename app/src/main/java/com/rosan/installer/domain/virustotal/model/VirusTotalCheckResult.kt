// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.domain.virustotal.model

sealed class VirusTotalCheckResult {
    abstract val sha256: String

    data class Safe(
        override val sha256: String,
        val malicious: Int,
        val suspicious: Int,
        val undetected: Int,
        val detailUrl: String
    ) : VirusTotalCheckResult()

    data class Risky(
        override val sha256: String,
        val malicious: Int,
        val suspicious: Int,
        val undetected: Int,
        val detailUrl: String
    ) : VirusTotalCheckResult()

    data class ApiError(
        override val sha256: String,
        val message: String,
        val statusCode: Int? = null,
        val detailUrl: String = ""
    ) : VirusTotalCheckResult()

    data class NetworkError(
        override val sha256: String,
        val message: String,
        val detailUrl: String = ""
    ) : VirusTotalCheckResult()
}
