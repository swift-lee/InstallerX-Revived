// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.preferred.virustotal

import com.rosan.installer.domain.settings.model.VirusTotalMode

sealed class VirusTotalSettingsAction {
    data class ChangeMode(val mode: VirusTotalMode) : VirusTotalSettingsAction()
    data class ChangeApiKey(val apiKey: String) : VirusTotalSettingsAction()
    data class ChangeEndpoint(val endpoint: String) : VirusTotalSettingsAction()
    data class DebugCheckSha256(val sha256: String) : VirusTotalSettingsAction()
    data object DebugClearResult : VirusTotalSettingsAction()
}
