// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2023-2026 iamr0s, InstallerX Revived contributors
package com.rosan.installer.ui.page.main.installer.dialog

import androidx.compose.material3.ButtonColors

data class DialogButton(
    val text: String,
    val weight: Float = 1f,
    val colors: ButtonColors? = null,
    val onLongClick: (() -> Unit)? = null,
    val onClick: () -> Unit
)
