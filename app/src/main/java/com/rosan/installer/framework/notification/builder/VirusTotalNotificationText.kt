// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.framework.notification.builder

import android.content.Context
import com.rosan.installer.R
import com.rosan.installer.domain.virustotal.model.VirusTotalCheckResult

fun VirusTotalCheckResult?.virusTotalNotificationTitle(context: Context): String = when (this) {
    is VirusTotalCheckResult.Safe -> context.getString(R.string.virus_total_prepare_safe)
    is VirusTotalCheckResult.Risky -> context.getString(R.string.virus_total_risky_app)
    is VirusTotalCheckResult.ApiError,
    is VirusTotalCheckResult.NetworkError -> context.getString(R.string.virus_total_check_failed_title)
    else -> context.getString(R.string.virus_total_check_failed_title)
}

fun VirusTotalCheckResult?.virusTotalNotificationText(context: Context): String = when (this) {
    is VirusTotalCheckResult.Safe -> context.getString(R.string.virus_total_prepare_safe)
    is VirusTotalCheckResult.Risky -> context.getString(
        R.string.virus_total_risky_reason_with_passed,
        malicious,
        suspicious,
        undetected,
    )
    is VirusTotalCheckResult.ApiError -> statusCode?.let {
        context.getString(R.string.virus_total_api_error_reason_with_status, message, it)
    } ?: context.getString(R.string.virus_total_api_error_reason, message)
    is VirusTotalCheckResult.NetworkError -> context.getString(R.string.virus_total_network_error_reason, message)
    else -> context.getString(R.string.virus_total_check_failed_title)
}
