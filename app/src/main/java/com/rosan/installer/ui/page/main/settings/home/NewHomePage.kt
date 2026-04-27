// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.add
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.TaskAlt
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rosan.installer.R
import com.rosan.installer.domain.device.model.ShizukuMode
import com.rosan.installer.domain.device.provider.DeviceCapabilityProvider
import com.rosan.installer.domain.settings.model.Authorizer
import com.rosan.installer.domain.settings.model.RootMode
import com.rosan.installer.ui.page.main.widget.util.OnLifecycleEvent
import com.rosan.installer.ui.theme.getMaterial3AppBarColor
import com.rosan.installer.ui.theme.installerMaterial3BlurEffect
import com.rosan.installer.ui.theme.rememberMaterial3BlurBackdrop
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import top.yukonga.miuix.kmp.blur.layerBackdrop

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun NewHomePage(
    useBlur: Boolean,
    viewModel: HomePageViewModel = koinViewModel(),
    outerPadding: PaddingValues,
    configCount: Int = 0
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val layoutDirection = LocalLayoutDirection.current
    val horizontalSafeInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal).asPaddingValues()
    val backdrop = rememberMaterial3BlurBackdrop(useBlur)

    OnLifecycleEvent(event = Lifecycle.Event.ON_RESUME) {
        viewModel.dispatch(HomePageViewAction.RefreshActivateStatus)
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeFlexibleTopAppBar(
                modifier = Modifier.installerMaterial3BlurEffect(backdrop),
                windowInsets = TopAppBarDefaults.windowInsets.add(WindowInsets(left = 12.dp)),
                title = { Text(text = stringResource(id = R.string.home)) },
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = backdrop.getMaterial3AppBarColor(),
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                    scrolledContainerColor = backdrop.getMaterial3AppBarColor()
                )
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .then(backdrop?.let { Modifier.layerBackdrop(it) } ?: Modifier),
            contentPadding = PaddingValues(
                start = 16.dp + horizontalSafeInsets.calculateStartPadding(layoutDirection),
                top = paddingValues.calculateTopPadding() + 16.dp,
                end = 16.dp + horizontalSafeInsets.calculateEndPadding(layoutDirection),
                bottom = outerPadding.calculateBottomPadding()
            )
        ) {
            item {
                if (uiState.activate) {
                    ActivateStatusCard()
                } else {
                    InActivateStatusCard()
                }
            }

            item {
                Spacer(modifier = Modifier.size(12.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.home_stat_authorizers),
                        value = uiState.availableAuthorizerCount.toString(),
                        containerColor = MaterialTheme.colorScheme.surfaceBright
                    )
                    StatCard(
                        modifier = Modifier.weight(1f),
                        title = stringResource(R.string.home_stat_profiles),
                        value = configCount.toString(),
                        containerColor = MaterialTheme.colorScheme.surfaceBright
                    )
                }
            }

            if (uiState.rootMode != RootMode.None || uiState.isSystemApp || uiState.shizukuAvailable) {
                item {
                    Spacer(modifier = Modifier.size(12.dp))
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        if (uiState.shizukuAvailable) {
                            val shizukuMode: ShizukuMode = if (LocalInspectionMode.current) {
                                ShizukuMode.NONE
                            } else {
                                koinInject<DeviceCapabilityProvider>().shizukuModeFlow.collectAsState().value
                            }
                            InfoChip(
                                text = stringResource(R.string.shizuku_working_mode, shizukuMode.name),
                                containerColor = if (uiState.shizukuAuthorized) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer
                            )
                        }
                        if (uiState.rootMode != RootMode.None) {
                            InfoChip(
                                text = stringResource(R.string.home_root_mode, uiState.rootMode.name),
                                containerColor = MaterialTheme.colorScheme.secondaryContainer
                            )
                        }
                        if (uiState.isSystemApp) {
                            InfoChip(
                                text = stringResource(R.string.home_system_app),
                                containerColor = MaterialTheme.colorScheme.tertiaryContainer
                            )
                        }
                    }
                }
            }

            item { Spacer(Modifier.navigationBarsPadding()) }
        }
    }
}

@Composable
private fun InfoChip(
    text: String,
    containerColor: Color
) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(containerColor)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    containerColor: Color
) {
    ElevatedCard(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = containerColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InActivateStatusCard() {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // TODO switch authorizer dialog
                }
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.TwoTone.Warning,
                contentDescription = stringResource(R.string.inactivate),
                tint = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier
                    .size(28.dp)
                    .padding(
                        horizontal = 4.dp
                    ),
            )
            Column(Modifier.padding(start = 20.dp)) {
                Text(
                    text = stringResource(R.string.working_status_not_default_installer),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
                Text(
                    text = stringResource(R.string.working_status_not_default_installer_desc),
                    style = MaterialTheme.typography.bodySmallEmphasized,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
            }
        }
    }
}


@Composable
private fun ActivateStatusCard() {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    // TODO switch authorizer dialog
                }
                .padding(24.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                Icons.TwoTone.TaskAlt,
                contentDescription = stringResource(R.string.activate),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier
                    .size(28.dp)
                    .padding(
                        horizontal = 4.dp
                    ),
            )
            Column(Modifier.padding(start = 20.dp)) {
                Text(
                    text = stringResource(R.string.working_status_default_installer),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = stringResource(R.string.working_status_default_installer_desc),
                    style = MaterialTheme.typography.bodySmallEmphasized,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
            }
        }
    }
}

@Composable
private fun PreviewOfActivateStatusCard() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Authorizer.entries.forEach {
            item {
                ActivateStatusCard()
            }
        }
    }
}

@Composable
private fun PreviewOfInActivateStatusCard() {
    LazyColumn(
        verticalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Authorizer.entries.forEach {
            item {
                InActivateStatusCard()
            }
        }
    }
}

@Preview(name = "preview")
@Composable
private fun InActivateStatusCardPreview() {
    PreviewOfInActivateStatusCard()
}

@Preview(name = "preview", locale = "zh-rCN")
@Composable
private fun InActivateStatusCardPreviewTranslate() {
    PreviewOfInActivateStatusCard()
}

@Preview(name = "preview")
@Composable
private fun ActivateStatusCardPreview() {
    PreviewOfActivateStatusCard()
}

@Preview(name = "preview", locale = "zh-rCN")
@Composable
private fun ActivateStatusCardPreviewTranslate() {
    PreviewOfActivateStatusCard()
}