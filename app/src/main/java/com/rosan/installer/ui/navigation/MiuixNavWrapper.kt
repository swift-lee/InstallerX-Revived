// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.navigation

import android.os.Build
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.RoomPreferences
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rosan.installer.R
import com.rosan.installer.domain.settings.model.ThemeState
import com.rosan.installer.ui.page.main.settings.SettingsSharedViewModel
import com.rosan.installer.ui.page.miuix.settings.SettingsCompactLayout
import com.rosan.installer.ui.page.miuix.settings.SettingsWideScreenLayout
import com.rosan.installer.ui.theme.LocalWindowLayoutInfo
import com.rosan.installer.ui.theme.WindowLayoutType
import com.rosan.installer.ui.theme.rememberMiuixBlurBackdrop
import top.yukonga.miuix.kmp.basic.NavigationItem
import top.yukonga.miuix.kmp.basic.SnackbarHostState

// Centralized complex layout logic to keep provider clean and readable
@Composable
fun MiuixMainPageWrapper(
    uiState: ThemeState,
    sharedViewModel: SettingsSharedViewModel
) {
    val layoutInfo = LocalWindowLayoutInfo.current
    val sharedState by sharedViewModel.state.collectAsStateWithLifecycle()
    val useBlur = uiState.useBlur
    val useFloatingBottomBar = uiState.useAppleFloatingBar
    val useFloatingBottomBarBlur =
        useBlur && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU

    val configLabel = stringResource(R.string.config)
    val preferredLabel = stringResource(R.string.preferred)

    val navigationItems = remember(configLabel, preferredLabel) {
        listOf(
            NavigationItem(
                label = configLabel,
                icon = Icons.Rounded.RoomPreferences
            ),
            NavigationItem(
                label = preferredLabel,
                icon = Icons.Rounded.Settings
            )
        )
    }

    val pagerState = rememberPagerState(
        initialPage = sharedState.lastMainPageIndex,
        pageCount = { navigationItems.size }
    )

    LaunchedEffect(pagerState.currentPage) {
        if (sharedState.lastMainPageIndex != pagerState.currentPage) {
            sharedViewModel.updateLastMainPageIndex(pagerState.currentPage)
        }
    }

    val snackbarHostState = remember { SnackbarHostState() }

    // Create separated backdrops for different blurred components
    val floatingBackdrop = com.kyant.backdrop.backdrops.rememberLayerBackdrop()
    val miuixBackdrop = rememberMiuixBlurBackdrop(useBlur)

    // Branch statically without layout delay traps
    if (layoutInfo.type == WindowLayoutType.EXPANDED || layoutInfo.showNavigationRail) {
        SettingsWideScreenLayout(
            pagerState = pagerState,
            navigationItems = navigationItems,
            snackbarHostState = snackbarHostState,
            useFloatingBottomBar = useFloatingBottomBar,
            useFloatingBottomBarBlur = useFloatingBottomBarBlur,
            floatingBackdrop = floatingBackdrop,
            miuixBackdrop = miuixBackdrop
        )
    } else {
        SettingsCompactLayout(
            pagerState = pagerState,
            navigationItems = navigationItems,
            snackbarHostState = snackbarHostState,
            useFloatingBottomBar = useFloatingBottomBar,
            useFloatingBottomBarBlur = useFloatingBottomBarBlur,
            floatingBackdrop = floatingBackdrop,
            miuixBackdrop = miuixBackdrop
        )
    }
}
