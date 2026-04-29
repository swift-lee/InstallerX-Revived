// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.installer.dialog.inner

import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ContainedLoadingIndicator
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rosan.installer.R
import com.rosan.installer.domain.virustotal.model.VirusTotalCheckResult
import com.rosan.installer.ui.page.main.installer.InstallerViewAction
import com.rosan.installer.ui.page.main.installer.InstallerViewModel
import com.rosan.installer.ui.page.main.installer.components.ErrorTextBlock
import com.rosan.installer.ui.page.main.installer.components.WarningTextBlock
import com.rosan.installer.ui.page.main.installer.components.workingIcon
import com.rosan.installer.ui.page.main.installer.dialog.DialogButton
import com.rosan.installer.ui.page.main.installer.dialog.DialogInnerParams
import com.rosan.installer.ui.page.main.installer.dialog.DialogParams
import com.rosan.installer.ui.page.main.installer.dialog.DialogParamsType
import com.rosan.installer.ui.page.main.installer.dialog.dialogButtons

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun virusTotalCheckingDialog(
    viewModel: InstallerViewModel,
): DialogParams {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val viewSettings = uiState.viewSettings

    return DialogParams(
        icon = DialogInnerParams(
            DialogParamsType.IconWorking.id,
            if (viewSettings.uiExpressive) {
                {
                    ContainedLoadingIndicator(
                        indicatorColor = MaterialTheme.colorScheme.primary,
                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                    )
                }
            } else workingIcon,
        ),
        title = DialogInnerParams(DialogParamsType.InstallerVirusTotalChecking.id) {
            Text(stringResource(R.string.virus_total_checking_install))
        },
        buttons = dialogButtons(DialogParamsType.ButtonsCancel.id) { emptyList() },
    )
}

@Composable
fun virusTotalBlockedDialog(
    viewModel: InstallerViewModel,
): DialogParams {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val result = uiState.virusTotalResult
    val baseParams = installInfoDialog(viewModel = viewModel, onTitleExtraClick = {})

    return when (result) {
        is VirusTotalCheckResult.Risky -> riskyVirusTotalDialog(baseParams, result, viewModel)
        is VirusTotalCheckResult.ApiError,
        is VirusTotalCheckResult.NetworkError -> errorVirusTotalDialog(baseParams, result, viewModel)
        else -> baseParams.copy(
            text = DialogInnerParams(DialogParamsType.InstallerVirusTotalBlocked.id) {
                Text(stringResource(R.string.virus_total_check_failed_title))
            },
            buttons = virusTotalErrorButtons(viewModel),
        )
    }
}

@Composable
private fun riskyVirusTotalDialog(
    baseParams: DialogParams,
    result: VirusTotalCheckResult.Risky,
    viewModel: InstallerViewModel,
): DialogParams = baseParams.copy(
    text = DialogInnerParams(DialogParamsType.InstallerVirusTotalBlocked.id) {
        WarningTextBlock(
            listOf(
                stringResource(
                    R.string.virus_total_risky_body,
                    result.malicious,
                    result.suspicious,
                    result.detailUrl,
                ) to MaterialTheme.colorScheme.error,
            ),
        )
    },
    buttons = virusTotalRiskyButtons(viewModel, result.detailUrl),
)

@Composable
private fun errorVirusTotalDialog(
    baseParams: DialogParams,
    result: VirusTotalCheckResult,
    viewModel: InstallerViewModel,
): DialogParams = baseParams.copy(
    text = DialogInnerParams(DialogParamsType.InstallerVirusTotalBlocked.id) {
        ErrorTextBlock(
            com.rosan.installer.domain.virustotal.exception.VirusTotalCheckException(result),
        )
    },
    buttons = virusTotalErrorButtons(viewModel),
)

@Composable
private fun virusTotalRiskyButtons(
    viewModel: InstallerViewModel,
    detailUrl: String,
): DialogInnerParams = dialogButtons(
    DialogParamsType.InstallerVirusTotalBlocked.id,
) {
    val uriHandler = LocalUriHandler.current
    listOf(
        DialogButton(stringResource(R.string.cancel)) {
            viewModel.dispatch(InstallerViewAction.ApproveVirusTotal(false))
        },
        DialogButton(
            text = stringResource(R.string.virus_total_continue_install),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFD32F2F),
                contentColor = Color.White,
            ),
        ) {
            viewModel.dispatch(InstallerViewAction.ApproveVirusTotal(true))
        },
        DialogButton(stringResource(R.string.details)) {
            if (detailUrl.isNotBlank()) uriHandler.openUri(detailUrl)
        },
    )
}

@Composable
private fun virusTotalErrorButtons(viewModel: InstallerViewModel): DialogInnerParams = dialogButtons(
    DialogParamsType.InstallerVirusTotalBlocked.id,
) {
    listOf(
        DialogButton(stringResource(R.string.virus_total_continue_install)) {
            viewModel.dispatch(InstallerViewAction.ApproveVirusTotal(true))
        },
        DialogButton(stringResource(R.string.cancel)) {
            viewModel.dispatch(InstallerViewAction.ApproveVirusTotal(false))
        },
    )
}
