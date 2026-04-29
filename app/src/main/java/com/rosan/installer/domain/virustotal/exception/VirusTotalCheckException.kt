// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.domain.virustotal.exception

import com.rosan.installer.R
import com.rosan.installer.domain.engine.exception.InstallerException
import com.rosan.installer.domain.virustotal.model.VirusTotalCheckResult

class VirusTotalCheckException(
    val result: VirusTotalCheckResult,
    val cancelled: Boolean = false,
) : InstallerException(result.toMessage()) {
    override fun getStringResId(): Int = when (result) {
        is VirusTotalCheckResult.Risky -> R.string.virus_total_risky_title
        is VirusTotalCheckResult.ApiError,
        is VirusTotalCheckResult.NetworkError -> R.string.virus_total_check_failed_title
        is VirusTotalCheckResult.Safe -> R.string.virus_total_settings
    }
}

private fun VirusTotalCheckResult.toMessage(): String = when (this) {
    is VirusTotalCheckResult.Safe -> "VirusTotal reported this APK as safe"
    is VirusTotalCheckResult.Risky -> buildString {
        append("VirusTotal reported a risky APK: ")
        append("malicious=$malicious, suspicious=$suspicious")
        if (detailUrl.isNotBlank()) append("\n$detailUrl")
    }
    is VirusTotalCheckResult.ApiError -> buildString {
        append(message)
        statusCode?.let { append(" (HTTP $it)") }
        if (detailUrl.isNotBlank()) append("\n$detailUrl")
    }
    is VirusTotalCheckResult.NetworkError -> buildString {
        append(message)
        if (detailUrl.isNotBlank()) append("\n$detailUrl")
    }
}
