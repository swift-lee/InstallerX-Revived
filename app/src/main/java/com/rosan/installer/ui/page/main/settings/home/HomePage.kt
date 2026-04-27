// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.twotone.TaskAlt
import androidx.compose.material.icons.twotone.Warning
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.rosan.installer.ui.page.main.widget.util.OnLifecycleEvent
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HomePage(
    outerPadding: PaddingValues,
    viewModel: HomePageViewModel = koinViewModel()
) {
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()
    val layoutDirection = LocalLayoutDirection.current
    val horizontalSafeInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal).asPaddingValues()

    OnLifecycleEvent(event = Lifecycle.Event.ON_RESUME) {
        viewModel.dispatch(HomePageViewAction.RefreshActivateStatus)
    }

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                scrollBehavior = scrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
                title = {
                    Text(text = stringResource(id = R.string.home))
                }
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            contentPadding = PaddingValues(
                start = 16.dp + horizontalSafeInsets.calculateStartPadding(layoutDirection),
                top = paddingValues.calculateTopPadding() + 16.dp,
                end = 16.dp + horizontalSafeInsets.calculateEndPadding(layoutDirection),
                bottom = outerPadding.calculateBottomPadding()
            )
        ) {
            item {
                if (uiState.activate) {
                    ActivateStatusCard(
                        state = uiState,
                    )
                } else {
                    InActivateStatusCard(
                        state = uiState,
                    )
                }
            }

            item { Spacer(Modifier.navigationBarsPadding()) }
        }
    }
}

@Composable
private fun InActivateStatusCard(
    state: MainPageViewState,
) {
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
                    text = stringResource(R.string.inactivate),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
                Text(
                    text = stringResource(R.string.activate_mode, state.globalAuthorizer.value),
                    style = MaterialTheme.typography.bodySmallEmphasized,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                )
                if (state.globalAuthorizer == Authorizer.Shizuku) {
                    if (!state.shizukuAuthorized) {
                        Text(
                            text = stringResource(R.string.shizuku_not_authorized),
                            style = MaterialTheme.typography.bodySmallEmphasized,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    } else if (!state.shizukuAvailable) {
                        Text(
                            text = stringResource(R.string.shizuku_not_available),
                            style = MaterialTheme.typography.bodySmallEmphasized,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                        )
                    }
                }
            }
        }
    }
}


@Composable
private fun ActivateStatusCard(
    state: MainPageViewState,
) {
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
                    text = stringResource(R.string.activate),
                    style = MaterialTheme.typography.titleMediumEmphasized,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                Text(
                    text = stringResource(R.string.activate_mode, state.globalAuthorizer.value),
                    style = MaterialTheme.typography.bodySmallEmphasized,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                )
                if (state.globalAuthorizer == Authorizer.Shizuku) {
                    val shizukuMode: ShizukuMode = if (LocalInspectionMode.current) {
                        // We are in Compose Preview, don't actually request the data
                        ShizukuMode.NONE
                    } else {
                        koinInject<DeviceCapabilityProvider>().shizukuModeFlow.collectAsState().value
                    }

                    Text(
                        text = stringResource(R.string.shizuku_working_mode, shizukuMode.name),
                        style = MaterialTheme.typography.bodySmallEmphasized,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                    )
                }
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
                ActivateStatusCard(
                    MainPageViewState(
                        globalAuthorizer = it,
                    )
                )
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
                InActivateStatusCard(
                    MainPageViewState(
                        globalAuthorizer = it,
                        shizukuAvailable = false,
                        shizukuAuthorized = false
                    )
                )
            }
            if (it == Authorizer.Shizuku) {
                item {
                    InActivateStatusCard(
                        MainPageViewState(
                            globalAuthorizer = it,
                            shizukuAvailable = false,
                            shizukuAuthorized = true
                        )
                    )
                }
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