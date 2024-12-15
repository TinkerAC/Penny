package app.penny.feature.setting

import app.penny.core.data.repository.UserPreferenceRepository
import app.penny.presentation.ui.LanguageManager
import app.penny.presentation.ui.ThemeManager
import app.penny.presentation.ui.ThemeState
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
        screenModelScope.launch {
            _uiState.value = _uiState.value.copy(
                theme = userPreferenceRepository.getThemeColor(),
                displayMode = userPreferenceRepository.getDisplayMode(),
                constraint = userPreferenceRepository.getConstraints(),
                language = userPreferenceRepository.getLanguage()
            )
        }

    }

    fun handleIntent(intent: SettingIntent) {
        when (intent) {
            is SettingIntent.SetTheme -> {
                _uiState.value = _uiState.value.copy(theme = intent.themeName)
                userPreferenceRepository.setThemeColor(intent.themeName)
                screenModelScope.launch {
                    ThemeManager.notifyThemeChange(
                        ThemeState(
                            themeConfig = intent.themeName.themeConfig,
                            displayMode = _uiState.value.displayMode,
                            constraints = _uiState.value.constraint
                        )
                    )
                }

            }

            is SettingIntent.SetConstraints -> {
                _uiState.value = _uiState.value.copy(constraint = intent.constraints)
                userPreferenceRepository.setConstraints(_uiState.value.constraint)
                screenModelScope.launch {
                    ThemeManager.notifyThemeChange(
                        ThemeState(
                            themeConfig = _uiState.value.theme.themeConfig,
                            displayMode = _uiState.value.displayMode,
                            constraints = intent.constraints
                        )
                    )
                }
            }

            is SettingIntent.SetDisplayMode -> {
                _uiState.value = _uiState.value.copy(displayMode = intent.darkMode)
                userPreferenceRepository.setDisplayMode(_uiState.value.displayMode)

                screenModelScope.launch {
                    ThemeManager.notifyThemeChange(
                        ThemeState(
                            themeConfig = _uiState.value.theme.themeConfig,
                            displayMode = intent.darkMode,
                            constraints = _uiState.value.constraint
                        )
                    )
                }
            }

            is SettingIntent.SetLanguage -> {
                _uiState.value = _uiState.value.copy(language = intent.language)
                userPreferenceRepository.setLanguage(_uiState.value.language)
                LanguageManager.setLocaleTo(_uiState.value.language)
                Logger.d { "Language set to ${_uiState.value.language}" }

            }
        }
    }


}
