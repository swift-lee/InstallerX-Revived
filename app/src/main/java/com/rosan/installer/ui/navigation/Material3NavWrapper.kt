// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2023-2026 iamr0s, InstallerX Revived contributors
package com.rosan.installer.ui.navigation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationItemIconPosition
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShortNavigationBar
import androidx.compose.material3.ShortNavigationBarArrangement
import androidx.compose.material3.ShortNavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material3.WideNavigationRail
import androidx.compose.material3.WideNavigationRailColors
import androidx.compose.material3.WideNavigationRailDefaults
import androidx.compose.material3.WideNavigationRailItem
import androidx.compose.material3.WideNavigationRailValue
import androidx.compose.material3.rememberWideNavigationRailState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rosan.installer.R
import com.rosan.installer.domain.settings.model.ThemeState
import com.rosan.installer.domain.settings.repository.ConfigRepository
import com.rosan.installer.ui.icons.AppIcons
import com.rosan.installer.ui.page.main.settings.SettingsSharedViewModel
import com.rosan.installer.ui.page.main.settings.config.all.AllPage
import com.rosan.installer.ui.page.main.settings.config.all.NewAllPage
import com.rosan.installer.ui.page.main.settings.home.HomePage
import com.rosan.installer.ui.page.main.settings.home.NewHomePage
import com.rosan.installer.ui.page.main.settings.preferred.NewPreferredPage
import com.rosan.installer.ui.page.main.settings.preferred.PreferredPage
import com.rosan.installer.ui.theme.LocalWindowLayoutInfo
import com.rosan.installer.ui.theme.installerMaterial3BlurEffect
import com.rosan.installer.ui.theme.rememberMaterial3BlurBackdrop
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.koin.compose.koinInject
import top.yukonga.miuix.kmp.blur.layerBackdrop

data class NavigationTab(
    val icon: ImageVector,
    val label: String
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Material3MainPageWrapper(
    uiState: ThemeState,
    sharedViewModel: SettingsSharedViewModel
) {
    val scope = rememberCoroutineScope()
    val sharedState by sharedViewModel.state.collectAsStateWithLifecycle()
    val showExpressiveUI = uiState.isExpressive
    val useBlur = showExpressiveUI && uiState.useBlur
    val backdrop = rememberMaterial3BlurBackdrop(useBlur)

    val configRepo = koinInject<ConfigRepository>()
    val configCountFlow = remember { configRepo.flowAll().map { it.size } }
    val configCount by configCountFlow.collectAsStateWithLifecycle(initialValue = 0)
    val homeLabel = stringResource(id = R.string.home)
    val homeIcon = ImageVector.vectorResource(R.drawable.ic_tile_icon)
    val configLabel = stringResource(R.string.config)
    val preferredLabel = stringResource(R.string.preferred)

    val tabs = remember(homeLabel, configLabel, preferredLabel) {
        listOf(
            NavigationTab(
                icon = homeIcon,
                label = homeLabel
            ),
            NavigationTab(
                icon = AppIcons.RoomPreferences,
                label = configLabel
            ),
            NavigationTab(
                icon = AppIcons.SettingsSuggest,
                label = preferredLabel
            )
        )
    }

    val pagerState = rememberPagerState(
        initialPage = sharedState.lastMainPageIndex,
        pageCount = { tabs.size }
    )
    val currentPage = pagerState.currentPage
    LaunchedEffect(currentPage) {
        if (sharedState.lastMainPageIndex != currentPage) {
            sharedViewModel.updateLastMainPageIndex(currentPage)
        }
    }
    fun onPageChanged(page: Int) {
        scope.launch {
            pagerState.animateScrollToPage(page = page)
        }
    }

    val layoutInfo = LocalWindowLayoutInfo.current
    val showRail = layoutInfo.showNavigationRail
    val isMedium = layoutInfo.isMediumPortrait

    val navigationSide =
        if (showRail) WindowInsetsSides.Start
        else WindowInsetsSides.Bottom

    val navigationWindowInsets = WindowInsets.safeDrawing.only(
        (if (showRail) WindowInsetsSides.Vertical
        else WindowInsetsSides.Horizontal) + navigationSide
    )

    if (!showRail) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            bottomBar = {
                RowNavigation(
                    modifier = Modifier.installerMaterial3BlurEffect(backdrop),
                    isM3e = showExpressiveUI,
                    windowInsets = navigationWindowInsets,
                    tabs = tabs,
                    currentPage = currentPage,
                    onPageChanged = { onPageChanged(it) },
                    configCount = configCount,
                    containerColor = if (useBlur) Color.Transparent else BottomAppBarDefaults.containerColor,
                    isMedium = isMedium
                )
            }
        ) { paddingValues ->
            HorizontalPager(
                state = pagerState,
                userScrollEnabled = true,
                modifier = Modifier
                    .fillMaxSize()
                    .then(backdrop?.let { Modifier.layerBackdrop(backdrop) } ?: Modifier)
            ) { page ->
                // Delegate page content rendering based on the current page index
                when (page) {
                    0 -> if (showExpressiveUI) {
                        NewHomePage(
                            useBlur = useBlur,
                            outerPadding = paddingValues,
                            configCount = configCount
                        )
                    } else {
                        HomePage(outerPadding = paddingValues)
                    }

                    1 -> if (showExpressiveUI) {
                        NewAllPage(useBlur = useBlur, outerPadding = paddingValues)
                    } else {
                        AllPage(outerPadding = paddingValues)
                    }

                    2 -> if (showExpressiveUI) {
                        NewPreferredPage(useBlur = useBlur, outerPadding = paddingValues)
                    } else {
                        PreferredPage(outerPadding = paddingValues)
                    }
                }
            }
        }
    } else {
        Row(modifier = Modifier.fillMaxSize()) {
            ColumnNavigation(
                isM3e = showExpressiveUI,
                windowInsets = navigationWindowInsets,
                tabs = tabs,
                currentPage = currentPage,
                onPageChanged = { onPageChanged(it) }
            )

            HorizontalPager(
                state = pagerState,
                userScrollEnabled = true,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize()
                    .then(backdrop?.let { Modifier.layerBackdrop(backdrop) } ?: Modifier)
            ) { page ->
                // Rail navigation doesn't overlay bottom content, so use zero padding
                val paddingValues = PaddingValues(0.dp)

                // Delegate page content rendering based on the current page index
                when (page) {
                    0 -> if (showExpressiveUI) {
                        NewHomePage(
                            useBlur = useBlur,
                            outerPadding = paddingValues,
                            configCount = configCount
                        )
                    } else {
                        HomePage(outerPadding = paddingValues)
                    }

                    1 -> if (showExpressiveUI) {
                        NewAllPage(useBlur = useBlur, outerPadding = paddingValues)
                    } else {
                        AllPage(outerPadding = paddingValues)
                    }

                    2 -> if (showExpressiveUI) {
                        NewPreferredPage(useBlur = useBlur, outerPadding = paddingValues)
                    } else {
                        PreferredPage(outerPadding = paddingValues)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun RowNavigation(
    modifier: Modifier = Modifier,
    isM3e: Boolean,
    windowInsets: WindowInsets,
    tabs: List<NavigationTab>,
    currentPage: Int,
    onPageChanged: (Int) -> Unit,
    configCount: Int,
    containerColor: Color = if (isM3e) MaterialTheme.colorScheme.surfaceContainer else BottomAppBarDefaults.containerColor,
    isMedium: Boolean = false
) {
    if (isM3e) {
        ShortNavigationBar(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentSize(),
            windowInsets = windowInsets,
            containerColor = containerColor,
            arrangement = if (isMedium) ShortNavigationBarArrangement.Centered else ShortNavigationBarArrangement.EqualWeight
        ) {
            tabs.forEachIndexed { index, navigationData ->
                ShortNavigationBarItem(
                    selected = currentPage == index,
                    onClick = { onPageChanged(index) },
                    iconPosition = if (isMedium) NavigationItemIconPosition.Start else NavigationItemIconPosition.Top,
                    icon = {
                        val showBadge = index == 1 && configCount > 1

                        BadgedBox(
                            badge = {
                                AnimatedVisibility(
                                    visible = showBadge,
                                    enter = scaleIn() + fadeIn(),
                                    exit = scaleOut() + fadeOut(),
                                    label = "badge"
                                ) {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.secondary,
                                        contentColor = MaterialTheme.colorScheme.onSecondary
                                    ) { Text(configCount.toString()) }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = navigationData.icon,
                                contentDescription = navigationData.label
                            )
                        }
                    },
                    label = {
                        Text(text = navigationData.label)
                    }
                )
            }
        }
    } else {
        // Fallback for non-expressive UI
        FlexibleBottomAppBar(
            modifier = modifier
                .fillMaxWidth()
                .wrapContentSize(),
            windowInsets = windowInsets,
            expandedHeight = 72.dp,
            containerColor = containerColor,
            horizontalArrangement = BottomAppBarDefaults.FlexibleHorizontalArrangement,
            content = {
                tabs.forEachIndexed { index, navigationTab ->
                    NavigationBarItem(
                        selected = currentPage == index,
                        onClick = { onPageChanged(index) },
                        icon = {
                            val showBadge = index == 1 && configCount > 1

                            BadgedBox(
                                badge = {
                                    ConfigBadge(
                                        showBadge = showBadge,
                                        configCount = configCount
                                    )
                                }
                            ) {
                                Icon(
                                    imageVector = navigationTab.icon,
                                    contentDescription = navigationTab.label
                                )
                            }
                        },
                        label = {
                            Text(text = navigationTab.label)
                        },
                        alwaysShowLabel = false
                    )
                }
            }
        )
    }
}

@Composable
fun ColumnNavigation(
    isM3e: Boolean,
    windowInsets: WindowInsets,
    tabs: List<NavigationTab>,
    currentPage: Int,
    onPageChanged: (Int) -> Unit
) {
    val state = rememberWideNavigationRailState()
    val scope = rememberCoroutineScope()

    WideNavigationRail(
        state = state,
        windowInsets = windowInsets,
        colors = if (isM3e) WideNavigationRailColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface,
            modalContainerColor = WideNavigationRailDefaults.colors().modalContainerColor,
            modalScrimColor = WideNavigationRailDefaults.colors().modalScrimColor,
            modalContentColor = WideNavigationRailDefaults.colors().modalContentColor
        ) else WideNavigationRailDefaults.colors(),
        header = {
            IconButton(
                modifier =
                    Modifier
                        .padding(start = 24.dp)
                        .semantics {
                            stateDescription =
                                if (state.currentValue == WideNavigationRailValue.Expanded) {
                                    "Expanded"
                                } else {
                                    "Collapsed"
                                }
                        },
                onClick = {
                    scope.launch {
                        if (state.targetValue == WideNavigationRailValue.Expanded)
                            state.collapse()
                        else state.expand()
                    }
                },
            ) {
                if (state.targetValue == WideNavigationRailValue.Expanded) {
                    Icon(AppIcons.MenuOpen, "Collapse rail")
                } else {
                    Icon(AppIcons.Menu, "Expand rail")
                }
            }
        }
    ) {
        tabs.forEachIndexed { index, navigationTab ->
            WideNavigationRailItem(
                railExpanded = state.targetValue == WideNavigationRailValue.Expanded,
                selected = currentPage == index,
                onClick = { onPageChanged(index) },
                icon = {
                    Icon(
                        imageVector = navigationTab.icon,
                        contentDescription = navigationTab.label
                    )
                },
                label = {
                    Text(text = navigationTab.label)
                }
            )
        }
    }
}

// Extract the badge into a separate Composable function
// This removes it from the RowScope context, solving the scope ambiguity
@Composable
private fun ConfigBadge(showBadge: Boolean, configCount: Int) {
    AnimatedVisibility(
        visible = showBadge,
        enter = scaleIn() + fadeIn(),
        exit = scaleOut() + fadeOut(),
        label = "badge"
    ) {
        Badge(
            containerColor = MaterialTheme.colorScheme.secondary,
            contentColor = MaterialTheme.colorScheme.onSecondary
        ) {
            Text(configCount.toString())
        }
    }
}
