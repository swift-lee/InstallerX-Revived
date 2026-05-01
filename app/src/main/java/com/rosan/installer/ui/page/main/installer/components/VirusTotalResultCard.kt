// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.installer.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.rosan.installer.R
import com.rosan.installer.domain.virustotal.model.VirusTotalCheckResult
import com.rosan.installer.ui.icons.AppIcons

@Composable
fun VirusTotalResultCard(
    result: VirusTotalCheckResult?,
    modifier: Modifier = Modifier,
) {
    result ?: return

    val color = when (result) {
        is VirusTotalCheckResult.Safe -> Color(0xFF2E7D32)
        is VirusTotalCheckResult.Risky -> MaterialTheme.colorScheme.error
        is VirusTotalCheckResult.ApiError,
        is VirusTotalCheckResult.NetworkError -> MaterialTheme.colorScheme.tertiary
    }
    val background = color.copy(alpha = 0.12f)
    val title = when (result) {
        is VirusTotalCheckResult.Safe -> stringResource(R.string.virus_total_prepare_safe)
        is VirusTotalCheckResult.Risky -> stringResource(R.string.virus_total_risky_title)
        is VirusTotalCheckResult.ApiError,
        is VirusTotalCheckResult.NetworkError -> stringResource(R.string.virus_total_check_failed_title)
    }
    val message = when (result) {
        is VirusTotalCheckResult.Safe -> stringResource(R.string.virus_total_prepare_safe_desc)
        is VirusTotalCheckResult.Risky -> stringResource(R.string.virus_total_risky_miuix_message)
        is VirusTotalCheckResult.ApiError -> result.message
        is VirusTotalCheckResult.NetworkError -> result.message
    }
    val detailUrl = when (result) {
        is VirusTotalCheckResult.Safe -> result.detailUrl
        is VirusTotalCheckResult.Risky -> result.detailUrl
        is VirusTotalCheckResult.ApiError -> result.detailUrl
        is VirusTotalCheckResult.NetworkError -> result.detailUrl
    }
    val uriHandler = LocalUriHandler.current

    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(background)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = if (result is VirusTotalCheckResult.Safe) AppIcons.AutoLockDefault else AppIcons.InstallBypassLowTargetSdk,
                contentDescription = null,
                tint = color
            )
            Text(
                text = title,
                color = color,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = message,
            color = color,
            style = MaterialTheme.typography.bodyMedium
        )
        if (result is VirusTotalCheckResult.Safe || result is VirusTotalCheckResult.Risky) {
            VirusTotalRiskRows(
                malicious = if (result is VirusTotalCheckResult.Risky) result.malicious else (result as VirusTotalCheckResult.Safe).malicious,
                suspicious = if (result is VirusTotalCheckResult.Risky) result.suspicious else (result as VirusTotalCheckResult.Safe).suspicious,
                passed = if (result is VirusTotalCheckResult.Risky) result.undetected else (result as VirusTotalCheckResult.Safe).undetected,
                color = color
            )
        }
        if (detailUrl.isNotBlank()) {
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.virus_total_check_on_virustotal),
                color = color,
                style = MaterialTheme.typography.labelLarge,
                modifier = Modifier.clickable { uriHandler.openUri(detailUrl) }
            )
        }
    }
}

@Composable
private fun VirusTotalRiskRows(
    malicious: Int,
    suspicious: Int,
    passed: Int,
    color: Color,
) {
    VirusTotalRiskRow(stringResource(R.string.virus_total_malicious_label), malicious.toString(), color)
    VirusTotalRiskRow(stringResource(R.string.virus_total_suspicious_label), suspicious.toString(), color)
    VirusTotalRiskRow(stringResource(R.string.virus_total_passed_label), passed.toString(), color)
}

@Composable
private fun VirusTotalRiskRow(label: String, value: String, color: Color) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text = label, color = color, style = MaterialTheme.typography.bodyMedium)
        Text(text = value, color = color, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold)
    }
}
