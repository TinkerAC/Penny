package app.penny.feature.setting

import app.penny.core.data.repository.UserPreferenceRepository
import app.penny.presentation.ui.LocaleManager
import app.penny.presentation.ui.ThemeManager
import app.penny.presentation.ui.ThemeState
import app.penny.presentation.ui.theme.AppTheme
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SettingViewModel(
    private val userPreferenceRepository: UserPreferenceRepository
) : ScreenModel {
    private val _uiState = MutableStateFlow(SettingUiState())
    val uiState: StateFlow<SettingUiState> = _uiState.asStateFlow()

    init {
        refreshData()
    }

    fun refreshData() {
        screenModelScope.launch {
            try {
                val storedDynamicTheme: AppTheme.DynamicAppTheme =
                    userPreferenceRepository.getStoredDynamicTheme()

                _uiState.value = _uiState.value.copy(
                    appTheme = userPreferenceRepository.getAppTheme(),
                    displayMode = userPreferenceRepository.getDisplayMode(),
                    constraint = userPreferenceRepository.getConstraints(),
                    language = userPreferenceRepository.getLanguage(),
                    appThemes = AppTheme.Static.items + (storedDynamicTheme),
                    autoCloudSyncEnabled = userPreferenceRepository.getAutoCloudSyncEnabled()

                )
            } catch (e: Exception) {
                Logger.e("Error initializing settings: ${e.message}")
            }
        }
    }

    fun handleIntent(intent: SettingIntent) {
        when (intent) {
            is SettingIntent.SetTheme -> {
                when (intent.appTheme) {
                    is AppTheme.Static -> {
                        screenModelScope.launch {
                            try {
                                _uiState.value = _uiState.value.copy(appTheme = intent.appTheme)
                                userPreferenceRepository.setAppTheme(intent.appTheme)
                                ThemeManager.notifyThemeChange(
                                    ThemeState(
                                        displayMode = _uiState.value.displayMode,
                                        constraints = _uiState.value.constraint,
                                        appTheme = _uiState.value.appTheme
                                    )
                                )
                            } catch (e: Exception) {
                                Logger.e("Error setting static theme: ${e.message}")
                            }
                        }
                    }

                    is AppTheme.DynamicAppTheme -> {
                        showColorPicker()
                        //limited by the design of AppTheme, the dynamic theme will be set after user select a color
                    }
                }
            }

            is SettingIntent.SetConstraints -> {
                screenModelScope.launch {
                    try {
                        _uiState.value = _uiState.value.copy(constraint = intent.constraints)
                        userPreferenceRepository.setConstraints(intent.constraints)

                        ThemeManager.notifyThemeChange(
                            ThemeState(
                                displayMode = _uiState.value.displayMode,
                                constraints = _uiState.value.constraint,
                                appTheme = _uiState.value.appTheme
                            )
                        )
                    } catch (e: Exception) {
                        Logger.e("Error setting constraints: ${e.message}")
                    }
                }
            }

            is SettingIntent.SetDisplayMode -> {
                screenModelScope.launch {
                    try {
                        _uiState.value = _uiState.value.copy(displayMode = intent.darkMode)
                        userPreferenceRepository.setDisplayMode(intent.darkMode)
                        ThemeManager.notifyThemeChange(
                            ThemeState(
                                displayMode = _uiState.value.displayMode,
                                constraints = _uiState.value.constraint,
                                appTheme = _uiState.value.appTheme
                            )
                        )
                    } catch (e: Exception) {
                        Logger.e("Error setting display mode: ${e.message}")
                    }
                }
            }

            is SettingIntent.SetLanguage -> {
                screenModelScope.launch {
                    try {
                        userPreferenceRepository.setLanguage(intent.language)
                        _uiState.value = _uiState.value.copy(language = intent.language)
                        LocaleManager.setLocaleTo(intent.language)
                        Logger.d { "Language set to ${intent.language}" }

                    } catch (e: Exception) {
                        Logger.e("Error setting language: ${e.message}")
                    }
                }
            }

            SettingIntent.HideColorPicker -> {
                _uiState.value = _uiState.value.copy(showColorPicker = false)
            }

            is SettingIntent.SetDynamicTheme -> {
                setDynamicTheme(intent.appTheme)
            }

            SettingIntent.ShowColorPicker -> {
                showColorPicker()
            }

            SettingIntent.ToggleAutoCloudSync -> {
                toggleAutoCloudSync()
            }
        }
    }

    private fun toggleAutoCloudSync() {
        screenModelScope.launch {
            try {
                val autoCloudSyncEnabled = _uiState.value.autoCloudSyncEnabled
                userPreferenceRepository.setAutoCloudSyncEnabled(!autoCloudSyncEnabled)
                _uiState.value = _uiState.value.copy(autoCloudSyncEnabled = !autoCloudSyncEnabled)
            } catch (e: Exception) {
                Logger.e("Error toggling auto cloud sync: ${e.message}")
            }
        }
    }


    private fun showColorPicker() {
        _uiState.value = _uiState.value.copy(showColorPicker = true)
    }

    private fun setDynamicTheme(appTheme: AppTheme.DynamicAppTheme) {
        screenModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(appTheme = appTheme, showColorPicker = false)
                userPreferenceRepository.setAppTheme(appTheme)
                ThemeManager.notifyThemeChange(
                    ThemeState(
                        displayMode = _uiState.value.displayMode,
                        constraints = _uiState.value.constraint,
                        appTheme = _uiState.value.appTheme
                    )
                )
            } catch (e: Exception) {
                Logger.e("Error setting dynamic theme: ${e.message}")
            }
        }
    }


    private fun recompose() {

    }
}