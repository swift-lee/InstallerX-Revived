// SPDX-License-Identifier: GPL-3.0-only
// Copyright (C) 2026 InstallerX Revived contributors
package com.rosan.installer.ui.page.main.settings.preferred.virustotal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rosan.installer.domain.settings.repository.AppSettingsRepository
import com.rosan.installer.domain.settings.repository.StringSetting
import com.rosan.installer.domain.settings.usecase.settings.UpdateSettingUseCase
import com.rosan.installer.domain.virustotal.model.VirusTotalCheckResult
import com.rosan.installer.domain.virustotal.repository.VirusTotalCheckerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VirusTotalSettingsViewModel(
    appSettingsRepo: AppSettingsRepository,
    private val updateSetting: UpdateSettingUseCase,
    private val checkerRepository: VirusTotalCheckerRepository
) : ViewModel() {
    private val debugState = MutableStateFlow(DebugState())

    val state: StateFlow<VirusTotalSettingsState> = combine(
        appSettingsRepo.preferencesFlow,
        debugState
    ) { prefs, debug ->
        VirusTotalSettingsState(
            mode = prefs.virusTotalMode,
            apiKey = prefs.virusTotalApiKey,
            endpoint = prefs.virusTotalEndpoint,
            debugChecking = debug.checking,
            debugResult = debug.result
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

            is VirusTotalSettingsAction.DebugCheckSha256 -> debugCheckSha256(action.sha256)

            VirusTotalSettingsAction.DebugClearResult -> {
                debugState.value = DebugState()
            }
        }
    }

    private fun debugCheckSha256(sha256: String) {
        val normalizedSha256 = sha256.trim().lowercase()
        if (!SHA256_REGEX.matches(normalizedSha256)) {
            debugState.value = DebugState(
                result = VirusTotalCheckResult.ApiError(
                    sha256 = normalizedSha256,
                    message = "Invalid SHA-256"
                )
            )
            return
        }

        viewModelScope.launch {
            val currentState = state.value
            if (currentState.apiKey.isBlank()) {
                debugState.value = DebugState(
                    result = VirusTotalCheckResult.ApiError(
                        sha256 = normalizedSha256,
                        message = "VirusTotal API key is not configured"
                    )
                )
                return@launch
            }

            debugState.value = DebugState(checking = true)
            val result = checkerRepository.checkSha256(
                sha256 = normalizedSha256,
                apiKey = currentState.apiKey.trim(),
                endpoint = normalizeEndpoint(currentState.endpoint)
            )
            debugState.value = DebugState(result = result)
        }
    }

    private fun normalizeEndpoint(endpoint: String): String = endpoint
        .trim()
        .ifBlank { DEFAULT_ENDPOINT }
        .trimEnd('/')

    private data class DebugState(
        val checking: Boolean = false,
        val result: VirusTotalCheckResult? = null
    )

    private companion object {
        val SHA256_REGEX = Regex("^[a-fA-F0-9]{64}$")
        const val DEFAULT_ENDPOINT = "https://www.virustotal.com/api/v3"
    }
}
