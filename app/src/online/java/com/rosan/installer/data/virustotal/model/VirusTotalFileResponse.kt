// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.data.virustotal.model

import kotlinx.serialization.Serializable

@Serializable
data class VirusTotalFileResponse(
    val data: VirusTotalFileData? = null
)

@Serializable
data class VirusTotalFileData(
    val attributes: VirusTotalFileAttributes? = null,
    val links: VirusTotalFileLinks? = null
)

@Serializable
data class VirusTotalFileAttributes(
    val last_analysis_stats: VirusTotalAnalysisStats? = null
)

@Serializable
data class VirusTotalAnalysisStats(
    val malicious: Int = 0,
    val suspicious: Int = 0,
    val undetected: Int = 0
)

@Serializable
data class VirusTotalFileLinks(
    val self: String? = null
)
