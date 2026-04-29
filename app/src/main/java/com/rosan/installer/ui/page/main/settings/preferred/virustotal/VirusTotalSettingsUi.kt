// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.preferred.virustotal

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.twotone.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LargeFlexibleTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rosan.installer.R
import com.rosan.installer.domain.settings.model.VirusTotalMode
import com.rosan.installer.ui.icons.AppIcons
import com.rosan.installer.ui.navigation.LocalNavigator
import com.rosan.installer.ui.navigation.Navigator
import com.rosan.installer.ui.navigation.Route
import com.rosan.installer.ui.page.main.settings.preferred.SettingsNavigationItemWidget
import com.rosan.installer.ui.page.main.widget.setting.AppBackButton
import com.rosan.installer.ui.page.main.widget.setting.BaseWidget
import com.rosan.installer.ui.page.main.widget.setting.DropDownMenuWidget
import com.rosan.installer.ui.page.main.widget.setting.SplicedColumnGroup
import com.rosan.installer.ui.page.miuix.settings.preferred.MiuixNavigationItemWidget
import com.rosan.installer.ui.theme.getMaterial3AppBarColor
import com.rosan.installer.ui.theme.getMiuixAppBarColor
import com.rosan.installer.ui.theme.installerMaterial3BlurEffect
import com.rosan.installer.ui.theme.none
import com.rosan.installer.ui.theme.rememberMaterial3BlurBackdrop
import org.koin.androidx.compose.koinViewModel
import top.yukonga.miuix.kmp.basic.BasicComponent
import top.yukonga.miuix.kmp.basic.Card
import top.yukonga.miuix.kmp.basic.MiuixScrollBehavior
import top.yukonga.miuix.kmp.basic.Scaffold as MiuixScaffold
import top.yukonga.miuix.kmp.basic.SmallTitle
import top.yukonga.miuix.kmp.basic.SpinnerEntry
import top.yukonga.miuix.kmp.basic.TopAppBar as MiuixTopAppBar
import top.yukonga.miuix.kmp.blur.layerBackdrop
import top.yukonga.miuix.kmp.preference.WindowSpinnerPreference
import top.yukonga.miuix.kmp.utils.overScrollVertical
import top.yukonga.miuix.kmp.utils.scrollEndHaptic

@Composable
fun VirusTotalSettingsEntry(navigator: Navigator) {
    SettingsNavigationItemWidget(
        icon = AppIcons.Security,
        title = stringResource(R.string.virus_total_settings),
        description = stringResource(R.string.virus_total_settings_desc),
        onClick = { navigator.push(Route.VirusTotalSettings) }
    )
}

@Composable
fun MiuixVirusTotalSettingsEntry(navigator: Navigator) {
    MiuixNavigationItemWidget(
        icon = AppIcons.Security,
        title = stringResource(R.string.virus_total_settings),
        description = stringResource(R.string.virus_total_settings_desc),
        onClick = { navigator.push(Route.VirusTotalSettings) }
    )
}

@Composable
fun VirusTotalSettingsRoute(
    useMiuix: Boolean,
    useBlur: Boolean
) {
    if (useMiuix) {
        MiuixVirusTotalSettingsPage(enableBlur = useBlur)
    } else {
        VirusTotalSettingsPage(useBlur = useBlur)
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun VirusTotalSettingsPage(
    useBlur: Boolean,
    viewModel: VirusTotalSettingsViewModel = koinViewModel()
) {
    val navigator = LocalNavigator.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val topAppBarState = rememberTopAppBarState()
    val scrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior(topAppBarState)
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var showEndpointDialog by remember { mutableStateOf(false) }
    var showCustomEndpointDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        topAppBarState.heightOffset = topAppBarState.heightOffsetLimit
    }

    if (showApiKeyDialog) {
        VirusTotalApiKeyDialog(
            initialApiKey = uiState.apiKey,
            onDismiss = { showApiKeyDialog = false },
            onConfirm = {
                showApiKeyDialog = false
                viewModel.dispatch(VirusTotalSettingsAction.ChangeApiKey(it))
            }
        )
    }

    if (showEndpointDialog) {
        VirusTotalEndpointSelectionDialog(
            useCustomEndpoint = uiState.endpoint.isNotBlank(),
            onDismiss = { showEndpointDialog = false },
            onConfirm = { useCustom ->
                showEndpointDialog = false
                if (useCustom) {
                    showCustomEndpointDialog = true
                } else {
                    viewModel.dispatch(VirusTotalSettingsAction.ChangeEndpoint(""))
                }
            }
        )
    }

    if (showCustomEndpointDialog) {
        VirusTotalCustomEndpointDialog(
            initialEndpoint = uiState.endpoint,
            onDismiss = { showCustomEndpointDialog = false },
            onConfirm = {
                showCustomEndpointDialog = false
                viewModel.dispatch(VirusTotalSettingsAction.ChangeEndpoint(it))
            }
        )
    }

    val layoutDirection = LocalLayoutDirection.current
    val horizontalSafeInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal).asPaddingValues()
    val backdrop = rememberMaterial3BlurBackdrop(useBlur)

    Scaffold(
        modifier = Modifier
            .nestedScroll(scrollBehavior.nestedScrollConnection)
            .fillMaxSize(),
        contentWindowInsets = WindowInsets.none,
        containerColor = MaterialTheme.colorScheme.surfaceContainer,
        topBar = {
            LargeFlexibleTopAppBar(
                modifier = Modifier.installerMaterial3BlurEffect(backdrop),
                windowInsets = TopAppBarDefaults.windowInsets.add(WindowInsets(left = 12.dp)),
                title = { Text(stringResource(R.string.virus_total_settings)) },
                navigationIcon = {
                    Row {
                        AppBackButton(
                            onClick = { navigator.pop() },
                            icon = Icons.AutoMirrored.TwoTone.ArrowBack,
                            modifier = Modifier.size(36.dp),
                            containerColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                    }
                },
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
                start = horizontalSafeInsets.calculateStartPadding(layoutDirection),
                top = paddingValues.calculateTopPadding(),
                end = horizontalSafeInsets.calculateEndPadding(layoutDirection),
                bottom = paddingValues.calculateBottomPadding()
            )
        ) {
            item {
                SplicedColumnGroup(title = stringResource(R.string.virus_total_settings)) {
                    item {
                        VirusTotalModeWidget(
                            currentMode = uiState.mode,
                            onModeChange = { viewModel.dispatch(VirusTotalSettingsAction.ChangeMode(it)) }
                        )
                    }
                    item {
                        VirusTotalApiKeyWidget(
                            configured = uiState.apiKey.isNotBlank(),
                            onClick = { showApiKeyDialog = true }
                        )
                    }
                    item {
                        VirusTotalEndpointWidget(
                            endpoint = uiState.endpoint,
                            onClick = { showEndpointDialog = true }
                        )
                    }
                }
            }
            item { Spacer(Modifier.navigationBarsPadding()) }
        }
    }
}

@Composable
private fun VirusTotalModeWidget(
    currentMode: VirusTotalMode,
    onModeChange: (VirusTotalMode) -> Unit
) {
    val modes = remember {
        listOf(
            VirusTotalMode.Disable,
            VirusTotalMode.Enable,
            VirusTotalMode.FollowConfig
        )
    }
    val options = modes.map { mode ->
        when (mode) {
            VirusTotalMode.Disable -> stringResource(R.string.virus_total_mode_disable)
            VirusTotalMode.Enable -> stringResource(R.string.virus_total_mode_enable)
            VirusTotalMode.FollowConfig -> stringResource(R.string.virus_total_mode_follow_config)
        }
    }
    val selectedIndex = modes.indexOf(currentMode).coerceAtLeast(0)
    val description = when (modes[selectedIndex]) {
        VirusTotalMode.Disable -> stringResource(R.string.virus_total_mode_disable_desc)
        VirusTotalMode.Enable -> stringResource(R.string.virus_total_mode_enable_desc)
        VirusTotalMode.FollowConfig -> stringResource(R.string.virus_total_mode_follow_config_desc)
    }

    DropDownMenuWidget(
        icon = AppIcons.Security,
        title = stringResource(R.string.virus_total_global_mode),
        description = description,
        choice = selectedIndex,
        data = options,
        onChoiceChange = { index ->
            val selectedMode = modes.getOrElse(index) { VirusTotalMode.Disable }
            if (currentMode != selectedMode) onModeChange(selectedMode)
        }
    )
}

@Composable
private fun VirusTotalApiKeyWidget(
    configured: Boolean,
    onClick: () -> Unit
) {
    BaseWidget(
        icon = AppIcons.BiometricAuth,
        title = stringResource(R.string.virus_total_api_key),
        description = stringResource(
            if (configured) R.string.virus_total_api_key_configured
            else R.string.virus_total_api_key_not_configured
        ),
        onClick = onClick
    ) {}
}

@Composable
private fun VirusTotalEndpointWidget(
    endpoint: String,
    onClick: () -> Unit
) {
    BaseWidget(
        icon = AppIcons.ViewSourceCode,
        title = stringResource(R.string.virus_total_endpoint),
        description = endpoint.ifBlank { stringResource(R.string.virus_total_endpoint_official) },
        onClick = onClick
    ) {}
}

@Composable
private fun MiuixVirusTotalSettingsPage(
    enableBlur: Boolean,
    viewModel: VirusTotalSettingsViewModel = koinViewModel()
) {
    val navigator = LocalNavigator.current
    val uiState by viewModel.state.collectAsStateWithLifecycle()
    val scrollBehavior = MiuixScrollBehavior()
    val layoutDirection = LocalLayoutDirection.current
    val horizontalSafeInsets = WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal).asPaddingValues()
    val topBarBackdrop = com.rosan.installer.ui.theme.rememberMiuixBlurBackdrop(enableBlur)
    var showApiKeyDialog by remember { mutableStateOf(false) }
    var showEndpointDialog by remember { mutableStateOf(false) }
    var showCustomEndpointDialog by remember { mutableStateOf(false) }

    if (showApiKeyDialog) {
        VirusTotalApiKeyDialog(
            initialApiKey = uiState.apiKey,
            onDismiss = { showApiKeyDialog = false },
            onConfirm = {
                showApiKeyDialog = false
                viewModel.dispatch(VirusTotalSettingsAction.ChangeApiKey(it))
            }
        )
    }

    if (showEndpointDialog) {
        VirusTotalEndpointSelectionDialog(
            useCustomEndpoint = uiState.endpoint.isNotBlank(),
            onDismiss = { showEndpointDialog = false },
            onConfirm = { useCustom ->
                showEndpointDialog = false
                if (useCustom) {
                    showCustomEndpointDialog = true
                } else {
                    viewModel.dispatch(VirusTotalSettingsAction.ChangeEndpoint(""))
                }
            }
        )
    }

    if (showCustomEndpointDialog) {
        VirusTotalCustomEndpointDialog(
            initialEndpoint = uiState.endpoint,
            onDismiss = { showCustomEndpointDialog = false },
            onConfirm = {
                showCustomEndpointDialog = false
                viewModel.dispatch(VirusTotalSettingsAction.ChangeEndpoint(it))
            }
        )
    }

    MiuixScaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            MiuixTopAppBar(
                modifier = Modifier.then(
                    topBarBackdrop?.let { Modifier.layerBackdrop(it) } ?: Modifier
                ),
                color = topBarBackdrop.getMiuixAppBarColor(),
                title = stringResource(R.string.virus_total_settings),
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    top.yukonga.miuix.kmp.basic.IconButton(onClick = { navigator.pop() }) {
                        top.yukonga.miuix.kmp.basic.Icon(
                            imageVector = Icons.AutoMirrored.TwoTone.ArrowBack,
                            contentDescription = null
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .then(topBarBackdrop?.let { Modifier.layerBackdrop(it) } ?: Modifier)
                .scrollEndHaptic()
                .overScrollVertical()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                start = horizontalSafeInsets.calculateStartPadding(layoutDirection),
                top = innerPadding.calculateTopPadding(),
                end = horizontalSafeInsets.calculateEndPadding(layoutDirection),
                bottom = innerPadding.calculateBottomPadding()
            ),
            overscrollEffect = null
        ) {
            item { SmallTitle(stringResource(R.string.virus_total_settings)) }
            item {
                Card(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .padding(bottom = 12.dp)
                ) {
                    val modes = remember {
                        listOf(
                            VirusTotalMode.Disable,
                            VirusTotalMode.Enable,
                            VirusTotalMode.FollowConfig
                        )
                    }
                    val entries = modes.map { mode ->
                        val text = when (mode) {
                            VirusTotalMode.Disable -> stringResource(R.string.virus_total_mode_disable)
                            VirusTotalMode.Enable -> stringResource(R.string.virus_total_mode_enable)
                            VirusTotalMode.FollowConfig -> stringResource(R.string.virus_total_mode_follow_config)
                        }
                        SpinnerEntry(title = text)
                    }
                    val selectedIndex = modes.indexOf(uiState.mode).coerceAtLeast(0)
                    val summary = when (modes[selectedIndex]) {
                        VirusTotalMode.Disable -> stringResource(R.string.virus_total_mode_disable_desc)
                        VirusTotalMode.Enable -> stringResource(R.string.virus_total_mode_enable_desc)
                        VirusTotalMode.FollowConfig -> stringResource(R.string.virus_total_mode_follow_config_desc)
                    }

                    WindowSpinnerPreference(
                        title = stringResource(R.string.virus_total_global_mode),
                        summary = summary,
                        items = entries,
                        selectedIndex = selectedIndex,
                        onSelectedIndexChange = { index ->
                            viewModel.dispatch(VirusTotalSettingsAction.ChangeMode(modes[index]))
                        }
                    )
                    BasicComponent(
                        title = stringResource(R.string.virus_total_api_key),
                        summary = stringResource(
                            if (uiState.apiKey.isNotBlank()) R.string.virus_total_api_key_configured
                            else R.string.virus_total_api_key_not_configured
                        ),
                        onClick = { showApiKeyDialog = true }
                    )
                    BasicComponent(
                        title = stringResource(R.string.virus_total_endpoint),
                        summary = uiState.endpoint.ifBlank { stringResource(R.string.virus_total_endpoint_official) },
                        onClick = { showEndpointDialog = true }
                    )
                }
            }
            item { Spacer(Modifier.navigationBarsPadding()) }
        }
    }
}

@Composable
private fun VirusTotalApiKeyDialog(
    initialApiKey: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var apiKey by remember { mutableStateOf(initialApiKey) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.virus_total_api_key)) },
        text = {
            OutlinedTextField(
                value = apiKey,
                onValueChange = { apiKey = it },
                label = { Text(stringResource(R.string.virus_total_api_key)) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(apiKey) }) {
                Text(stringResource(R.string.confirm))
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
private fun VirusTotalEndpointSelectionDialog(
    useCustomEndpoint: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (Boolean) -> Unit
) {
    var useCustom by remember { mutableStateOf(useCustomEndpoint) }
    val options = listOf(
        false to stringResource(R.string.virus_total_endpoint_official),
        true to stringResource(R.string.virus_total_endpoint_custom)
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.virus_total_endpoint)) },
        text = {
            Column(Modifier.selectableGroup()) {
                options.forEach { (custom, label) ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .selectable(
                                selected = custom == useCustom,
                                onClick = { useCustom = custom },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = custom == useCustom,
                            onClick = null
                        )
                        Text(
                            text = label,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(useCustom) }) {
                Text(stringResource(R.string.confirm))
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
private fun VirusTotalCustomEndpointDialog(
    initialEndpoint: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var endpoint by remember { mutableStateOf(initialEndpoint) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(stringResource(R.string.virus_total_endpoint_custom)) },
        text = {
            OutlinedTextField(
                value = endpoint,
                onValueChange = { endpoint = it },
                label = { Text(stringResource(R.string.virus_total_endpoint)) },
                singleLine = true
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(endpoint) }) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
