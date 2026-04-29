// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.preferred.virustotal

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import com.rosan.installer.R
import com.rosan.installer.domain.virustotal.model.VirusTotalCheckResult
import com.rosan.installer.ui.icons.AppIcons
import com.rosan.installer.ui.page.main.widget.setting.BaseWidget

@Composable
fun VirusTotalDebugCheckWidget(
    checking: Boolean,
    onClick: () -> Unit
) {
    BaseWidget(
        icon = AppIcons.Search,
        title = stringResource(R.string.virus_total_debug_check),
        description = stringResource(
            if (checking) R.string.virus_total_debug_checking
            else R.string.virus_total_debug_check_desc
        ),
        enabled = !checking,
        onClick = onClick
    ) {}
}

@Composable
fun VirusTotalDebugCheckDialog(
    checking: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var sha256 by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.virus_total_debug_check)) },
        text = {
            OutlinedTextField(
                value = sha256,
                onValueChange = { sha256 = it },
                label = { Text(stringResource(R.string.virus_total_debug_sha256)) },
                singleLine = true,
                enabled = !checking
            )
        },
        confirmButton = {
            TextButton(
                enabled = !checking,
                onClick = { onConfirm(sha256) }
            ) {
                Text(
                    stringResource(
                        if (checking) R.string.virus_total_debug_checking
                        else R.string.confirm
                    )
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}

@Composable
fun VirusTotalDebugResultDialog(
    result: VirusTotalCheckResult,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.virus_total_debug_result)) },
        text = { Text(formatDebugResult(result)) },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.confirm))
            }
        }
    )
}

private fun formatDebugResult(result: VirusTotalCheckResult): String = when (result) {
    is VirusTotalCheckResult.Safe -> "Safe\n" +
            "SHA-256: ${result.sha256}\n" +
            "Malicious=${result.malicious}, Suspicious=${result.suspicious}, Passed=${result.undetected}\n" +
            "detail=${result.detailUrl}"

    is VirusTotalCheckResult.Risky -> "Risky\n" +
            "SHA-256: ${result.sha256}\n" +
            "malicious=${result.malicious}, suspicious=${result.suspicious}, undetected=${result.undetected}\n" +
            "detail=${result.detailUrl}"

    is VirusTotalCheckResult.ApiError -> "API error\n" +
            "SHA-256: ${result.sha256}\n" +
            "status=${result.statusCode ?: ""}\n" +
            result.message +
            result.detailUrl.takeIf { it.isNotBlank() }?.let { "\ndetail=$it" }.orEmpty()

    is VirusTotalCheckResult.NetworkError -> "Network error\n" +
            "SHA-256: ${result.sha256}\n" +
            result.message +
            result.detailUrl.takeIf { it.isNotBlank() }?.let { "\ndetail=$it" }.orEmpty()
}
