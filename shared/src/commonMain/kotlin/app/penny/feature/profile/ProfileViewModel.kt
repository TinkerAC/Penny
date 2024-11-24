package app.penny.feature.profile

import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.usecase.CheckIsEmailRegisteredUseCase
import app.penny.core.domain.usecase.LoginUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ProfileViewModel(
    private val userDataRepository: UserDataRepository,
    private val checkIsEmailRegisteredUseCase: CheckIsEmailRegisteredUseCase,
    private val loginUseCase: LoginUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            fetchProfileStatistics()
        }
    }

    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.TryLogin -> tryLogin()
            is ProfileIntent.Login -> login(intent.username, intent.password)
            is ProfileIntent.UnfocusEmail -> unfocusUsername(intent.username)
            ProfileIntent.DismissLoginModal -> dismissLoginModal()
        }
    }

    private fun unfocusUsername(username: String) {
        screenModelScope.launch {
            val isUsernameValid = checkIsEmailRegisteredUseCase(username)
            _uiState.value = _uiState.value.copy(
                isUsernameValid = isUsernameValid
            )
        }
    }

    private fun login(username: String, password: String) {
        screenModelScope.launch {
            try {
                val result = loginUseCase(username, password)
                // Handle successful login
                if (result.success) {
                    _uiState.value = _uiState.value.copy(

                        loggingModalVisible = false,
                        name = result.userDto?.username ?: "",
                        errorMessage = null
                    )
                    fetchProfileStatistics()
                } else {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Invalid email or password."
                    )
                }

            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = "An error occurred during login."
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun fetchProfileStatistics() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        _uiState.value = _uiState.value.copy(
            userUuid = Uuid.parse(
                userDataRepository.getUserUuid()
            )
        )
    }

    private fun tryLogin() {
        _uiState.value = _uiState.value.copy(
            loggingModalVisible = true
        )
    }

    private fun dismissLoginModal() {
        _uiState.value = _uiState.value.copy(
            loggingModalVisible = false,
            errorMessage = null
        )
    }
}