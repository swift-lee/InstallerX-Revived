// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.home

import android.content.pm.PackageManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rosan.installer.domain.settings.model.Authorizer
import com.rosan.installer.domain.settings.repository.AppSettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import rikka.shizuku.Shizuku

class HomePageViewModel(
    appSettingsRepo: AppSettingsRepository,
) : ViewModel() {
    private val shizukuStatusFlow = MutableStateFlow(getShizukuStatus())

    val state: StateFlow<MainPageViewState> = combine(
        appSettingsRepo.preferencesFlow,
        shizukuStatusFlow
    ) { prefs, (available, authorized) ->
        MainPageViewState(
            globalAuthorizer = prefs.authorizer,
            activate = prefs.authorizer != Authorizer.Shizuku || (available && authorized),
            shizukuAvailable = available,
            shizukuAuthorized = authorized
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = MainPageViewState()
    )

    fun dispatch(action: HomePageViewAction) {
        when (action) {
            is HomePageViewAction.RefreshActivateStatus -> {
                shizukuStatusFlow.value = getShizukuStatus()
            }
        }
    }

    private fun getShizukuStatus(): Pair<Boolean, Boolean> {
        val shizukuAvailable = try {
            Shizuku.pingBinder()
        } catch (_: Exception) {
            false
        }

        val shizukuAuthorized = if (shizukuAvailable) {
            try {
                Shizuku.checkSelfPermission() == PackageManager.PERMISSION_GRANTED
            } catch (_: Exception) {
                false
            }
        } else {
            false
        }
        return shizukuAvailable to shizukuAuthorized
    }
}
