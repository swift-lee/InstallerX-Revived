// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.home

import com.rosan.installer.domain.settings.model.Authorizer
import com.rosan.installer.domain.settings.model.RootMode

class MainPageViewState(
    val globalAuthorizer: Authorizer = Authorizer.Shizuku,
    val activate: Boolean = false,
    val isDefaultInstaller: Boolean = false,
    val shizukuAvailable: Boolean = false,
    val shizukuAuthorized: Boolean = false,
    val rootMode: RootMode = RootMode.None,
    val isSystemApp: Boolean = false,
    val availableAuthorizerCount: Int = 0
)
