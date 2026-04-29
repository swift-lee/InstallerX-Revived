// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.installer.dialog.inner

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.rosan.installer.R
import com.rosan.installer.ui.page.main.installer.InstallerViewAction
import com.rosan.installer.ui.page.main.installer.InstallerViewModel
import com.rosan.installer.ui.page.main.installer.dialog.DialogButton
import com.rosan.installer.ui.page.main.installer.dialog.DialogInnerParams
import com.rosan.installer.ui.page.main.installer.dialog.DialogParams
import com.rosan.installer.ui.page.main.installer.dialog.DialogParamsType
import com.rosan.installer.ui.page.main.installer.dialog.dialogButtons

@Composable
fun virusTotalCancelledDialog(
    viewModel: InstallerViewModel,
): DialogParams {
    val baseParams = installInfoDialog(viewModel = viewModel, onTitleExtraClick = {})
    return baseParams.copy(
        text = DialogInnerParams(DialogParamsType.InstallerVirusTotalBlocked.id) {
            Text(stringResource(R.string.virus_total_cancelled))
        },
        buttons = dialogButtons(DialogParamsType.InstallerVirusTotalBlocked.id) {
            listOf(
                DialogButton(stringResource(R.string.finish)) {
                    viewModel.dispatch(InstallerViewAction.Close)
                },
            )
        },
    )
}
