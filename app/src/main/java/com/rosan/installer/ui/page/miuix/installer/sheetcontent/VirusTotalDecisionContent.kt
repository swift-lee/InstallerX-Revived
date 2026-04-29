// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.miuix.installer.sheetcontent

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rosan.installer.R
import com.rosan.installer.domain.virustotal.model.VirusTotalCheckResult
import com.rosan.installer.framework.notification.builder.virusTotalNotificationText
import com.rosan.installer.framework.notification.builder.virusTotalNotificationTitle
import com.rosan.installer.ui.icons.AppIcons
import com.rosan.installer.ui.page.main.installer.InstallerViewAction
import com.rosan.installer.ui.page.main.installer.InstallerViewModel
import com.rosan.installer.ui.util.isGestureNavigation
import top.yukonga.miuix.kmp.basic.ButtonDefaults
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.CardColors
import top.yukonga.miuix.kmp.basic.Icon
import top.yukonga.miuix.kmp.basic.Text
import top.yukonga.miuix.kmp.basic.TextButton
import top.yukonga.miuix.kmp.theme.MiuixTheme
import top.yukonga.miuix.kmp.theme.MiuixTheme.isDynamicColor

@Composable
fun VirusTotalBlockedContent(
    viewModel: InstallerViewModel,
    onClose: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val result = uiState.virusTotalResult

    if (result is VirusTotalCheckResult.Risky) {
        VirusTotalRiskyContent(
            result = result,
            onCancel = {
                viewModel.dispatch(InstallerViewAction.ApproveVirusTotal(false))
                onClose()
            },
            onContinue = { viewModel.dispatch(InstallerViewAction.ApproveVirusTotal(true)) },
        )
    } else {
        VirusTotalNonRiskContent(
            result = result,
            onCancel = {
                viewModel.dispatch(InstallerViewAction.ApproveVirusTotal(false))
                onClose()
            },
            onContinue = { viewModel.dispatch(InstallerViewAction.ApproveVirusTotal(true)) },
        )
    }
}

@Composable
private fun VirusTotalRiskyContent(
    result: VirusTotalCheckResult.Risky,
    onCancel: () -> Unit,
    onContinue: () -> Unit,
) {
    val uriHandler = LocalUriHandler.current
    val isDark = isSystemInDarkTheme()
    val cardBackground = if (isDark) Color(0xFF8E2B2B) else Color(0xFFE57373)
    val actionRed = Color(0xFFE60000)

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(actionRed),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = AppIcons.Tip,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(40.dp),
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.virus_total_risky_title),
            style = MiuixTheme.textStyles.title2,
            color = MiuixTheme.colorScheme.onSurface,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
            colors = CardColors(
                color = cardBackground,
                contentColor = Color.White,
            ),
            cornerRadius = 16.dp,
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                VirusTotalRiskInfoRow(
                    label = stringResource(R.string.virus_total_malicious_label),
                    value = result.malicious.toString(),
                )
                VirusTotalRiskInfoRow(
                    label = stringResource(R.string.virus_total_suspicious_label),
                    value = result.suspicious.toString(),
                )
                VirusTotalRiskInfoRow(
                    label = stringResource(R.string.virus_total_passed_label),
                    value = result.undetected.toString(),
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        TextButton(
            onClick = {
                if (result.detailUrl.isNotBlank()) uriHandler.openUri(result.detailUrl)
            },
            text = stringResource(R.string.virus_total_check_on_virustotal),
            modifier = Modifier.fillMaxWidth(),
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(top = 6.dp, bottom = if (isGestureNavigation()) 24.dp else 0.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TextButton(
                onClick = onCancel,
                text = stringResource(R.string.cancel),
                colors = ButtonDefaults.textButtonColors(
                    color = if (isDynamicColor) MiuixTheme.colorScheme.secondaryContainer else MiuixTheme.colorScheme.secondaryVariant,
                    textColor = if (isDynamicColor) MiuixTheme.colorScheme.onSecondaryContainer else MiuixTheme.colorScheme.onSecondaryVariant,
                ),
                modifier = Modifier.weight(1f),
            )
            TextButton(
                onClick = onContinue,
                text = stringResource(R.string.continue_action),
                colors = ButtonDefaults.textButtonColors(
                    color = actionRed,
                    textColor = Color.White,
                ),
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun VirusTotalRiskInfoRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MiuixTheme.textStyles.body2,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = value,
            style = MiuixTheme.textStyles.body2,
            color = Color.White,
            textAlign = TextAlign.End,
        )
    }
}

@Composable
private fun VirusTotalNonRiskContent(
    result: VirusTotalCheckResult?,
    onCancel: () -> Unit,
    onContinue: () -> Unit,
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = result.virusTotalNotificationTitle(context) + "\n" +
                result.virusTotalNotificationText(context),
            style = MiuixTheme.textStyles.body1,
            color = MiuixTheme.colorScheme.onSurface,
        )
        TextButton(
            onClick = onContinue,
            text = stringResource(R.string.continue_action),
            colors = ButtonDefaults.textButtonColorsPrimary(),
            modifier = Modifier.fillMaxWidth(),
        )
        TextButton(
            onClick = onCancel,
            text = stringResource(R.string.cancel),
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
