// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.preferred.virustotal

import com.rosan.installer.domain.settings.model.VirusTotalMode
import com.rosan.installer.domain.virustotal.model.VirusTotalCheckResult

data class VirusTotalSettingsState(
    val mode: VirusTotalMode = VirusTotalMode.Disable,
    val apiKey: String = "",
    val endpoint: String = "",
    val debugChecking: Boolean = false,
    val debugResult: VirusTotalCheckResult? = null
)
