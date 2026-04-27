// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.home

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rosan.installer.domain.device.provider.DeviceCapabilityProvider
import com.rosan.installer.domain.settings.model.RootMode
import com.rosan.installer.domain.settings.repository.AppSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import rikka.shizuku.Shizuku

class HomePageViewModel(
    appSettingsRepo: AppSettingsRepository,
    private val capabilityProvider: DeviceCapabilityProvider
) : ViewModel() {
    private val refreshFlow = MutableStateFlow(0)

    val state: StateFlow<MainPageViewState> = combine(
        appSettingsRepo.preferencesFlow,
        capabilityProvider.shizukuModeFlow,
        capabilityProvider.rootModeFlow,
        refreshFlow
    ) { prefs, shizukuMode, rootMode, _ ->
        val isDefault = capabilityProvider.isDefaultInstaller
        val shizukuAvailable = shizukuMode != com.rosan.installer.domain.device.model.ShizukuMode.NONE
        val shizukuAuthorized = if (shizukuAvailable) {
            try {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            } catch (_: Exception) {
                false
            }
        } else {
            false
        }

        var availableCount = 0
        if (shizukuAvailable && shizukuAuthorized) availableCount++
        if (rootMode != RootMode.None) availableCount++
        if (capabilityProvider.isSystemApp) availableCount++

        MainPageViewState(
            globalAuthorizer = prefs.authorizer,
            activate = isDefault,
            isDefaultInstaller = isDefault,
            shizukuAvailable = shizukuAvailable,
            shizukuAuthorized = shizukuAuthorized,
            rootMode = rootMode,
            isSystemApp = capabilityProvider.isSystemApp,
            availableAuthorizerCount = availableCount
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MainPageViewState()
    )

    fun dispatch(action: HomePageViewAction) {
        when (action) {
            is HomePageViewAction.RefreshActivateStatus -> {
                capabilityProvider.refreshPrivilegeStatus()
                refreshFlow.value++
            }
        }
    }
}
