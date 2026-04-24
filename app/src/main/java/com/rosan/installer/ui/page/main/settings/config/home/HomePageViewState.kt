// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.config.home

import com.rosan.installer.domain.settings.model.Authorizer

enum class UiStyle {
    Material,
    MaterialExpressive,
    Miuix
}

class MainPageViewState(
    val globalAuthorizer: Authorizer = Authorizer.Shizuku,
    val activate: Boolean = false,
    val shizukuAvailable: Boolean = false,
    val shizukuAuthorized: Boolean = false,
    val style: UiStyle = UiStyle.MaterialExpressive
)