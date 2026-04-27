// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.home

sealed class HomePageViewAction {
    data object RefreshActivateStatus : HomePageViewAction()
}