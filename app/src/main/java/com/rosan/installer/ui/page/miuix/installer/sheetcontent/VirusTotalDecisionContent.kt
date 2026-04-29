// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.miuix.installer.sheetcontent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rosan.installer.R
import com.rosan.installer.domain.virustotal.model.VirusTotalCheckResult
import com.rosan.installer.ui.page.main.installer.InstallerViewAction
import com.rosan.installer.ui.page.main.installer.InstallerViewModel
import top.yukonga.miuix.kmp.basic.ButtonDefaults as MiuixButtonDefaults
import top.yukonga.miuix.kmp.basic.TextButton

@Composable
fun VirusTotalBlockedContent(
    viewModel: InstallerViewModel,
    onClose: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val result = uiState.virusTotalResult
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = when (result) {
                is VirusTotalCheckResult.Risky -> stringResource(
                    R.string.virus_total_risky_body,
                    result.malicious,
                    result.suspicious,
                    result.detailUrl,
                )
                is VirusTotalCheckResult.ApiError -> result.message
                is VirusTotalCheckResult.NetworkError -> result.message
                else -> stringResource(R.string.virus_total_check_failed_title)
            },
            style = MaterialTheme.typography.bodyLarge,
            color = if (result is VirusTotalCheckResult.Risky) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface,
        )

        if (result is VirusTotalCheckResult.Risky) {
            TextButton(
                onClick = {
                    viewModel.dispatch(InstallerViewAction.ApproveVirusTotal(false))
                    onClose()
                },
                text = stringResource(R.string.cancel),
                modifier = Modifier.fillMaxWidth(),
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                androidx.compose.material3.Button(
                    onClick = { viewModel.dispatch(InstallerViewAction.ApproveVirusTotal(true)) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFD32F2F),
                        contentColor = Color.White,
                    ),
                ) {
                    Text(stringResource(R.string.virus_total_continue_install))
                }
                TextButton(
                    onClick = {
                        if (result.detailUrl.isNotBlank()) uriHandler.openUri(result.detailUrl)
                    },
                    text = stringResource(R.string.details),
                    modifier = Modifier.weight(1f),
                )
            }
        } else {
            TextButton(
                onClick = { viewModel.dispatch(InstallerViewAction.ApproveVirusTotal(true)) },
                text = stringResource(R.string.virus_total_continue_install),
                colors = MiuixButtonDefaults.textButtonColorsPrimary(),
                modifier = Modifier.fillMaxWidth(),
            )
            TextButton(
                onClick = {
                    viewModel.dispatch(InstallerViewAction.ApproveVirusTotal(false))
                    onClose()
                },
                text = stringResource(R.string.cancel),
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
