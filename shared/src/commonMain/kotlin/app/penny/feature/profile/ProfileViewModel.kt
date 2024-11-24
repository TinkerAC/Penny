package app.penny.feature.profile

import app.penny.core.data.repository.AuthRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.impl.AuthRepositoryImpl
import app.penny.core.domain.usecase.CheckIsEmailRegisteredUseCase
import app.penny.core.domain.usecase.LoginUseCase
import app.penny.core.domain.usecase.RegisterUseCase
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
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository

) : ScreenModel {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            fetchProfileStatistics()

            _uiState.value = _uiState.value.copy(
                isLoggedIn = authRepository.isLoggedIn()
            )

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

    private fun unfocusUsername(email: String) {
        screenModelScope.launch {
            val isEmailRegistered = checkIsEmailRegisteredUseCase(email)

            if (!isEmailRegistered) {
                // register and login
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Account will be created for you."
                )

            } else {
                // login
                _uiState.value = _uiState.value.copy(
                    errorMessage = "Welcome back!"
                )
            }


        }
    }

    private fun login(email: String, password: String) {
        screenModelScope.launch {
            try {

                val isEmailRegistered = checkIsEmailRegisteredUseCase(email)

                if (!isEmailRegistered) {
                    // register and login
                    val registerResponse = registerUseCase(email, password)
                    if (registerResponse.success) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Account created successfully."
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "An error occurred during registration."
                        )

                    }

                    val loginResponse = loginUseCase(email, password)
                    if (loginResponse.success) {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "Login successful."
                        )
                        handleIntent(ProfileIntent.DismissLoginModal)
                    } else {
                        _uiState.value = _uiState.value.copy(
                            errorMessage = "An error occurred during login."
                        )
                    }


                }
                val result = loginUseCase(email, password)
                // Handle successful login
                if (result.success) {
                    _uiState.value = _uiState.value.copy(

                        loggingModalVisible = false,
                        username = result.userDto?.username ?: "",
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
            ),
            email = userDataRepository.getUserEmailOrNull(),
            username = userDataRepository.getUserNameOrNull(),
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