// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.preferred.virustotal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rosan.installer.domain.settings.repository.AppSettingsRepository
import com.rosan.installer.domain.settings.repository.StringSetting
import com.rosan.installer.domain.settings.usecase.settings.UpdateSettingUseCase
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VirusTotalSettingsViewModel(
    appSettingsRepo: AppSettingsRepository,
    private val updateSetting: UpdateSettingUseCase
) : ViewModel() {

    val state: StateFlow<VirusTotalSettingsState> = appSettingsRepo.preferencesFlow.map { prefs ->
        VirusTotalSettingsState(
            mode = prefs.virusTotalMode,
            apiKey = prefs.virusTotalApiKey,
            endpoint = prefs.virusTotalEndpoint
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = VirusTotalSettingsState()
    )

    fun dispatch(action: VirusTotalSettingsAction) {
        when (action) {
            is VirusTotalSettingsAction.ChangeMode -> viewModelScope.launch {
                updateSetting(StringSetting.VirusTotalMode, action.mode.value)
            }

            is VirusTotalSettingsAction.ChangeApiKey -> viewModelScope.launch {
                updateSetting(StringSetting.VirusTotalApiKey, action.apiKey)
            }

            is VirusTotalSettingsAction.ChangeEndpoint -> viewModelScope.launch {
                updateSetting(StringSetting.VirusTotalEndpoint, action.endpoint)
            }
        }
    }
}
