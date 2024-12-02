package app.penny.feature.profile

import app.penny.core.data.repository.AuthRepository
import app.penny.core.data.repository.UserDataRepository
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
            ProfileIntent.NavigateToSettings -> navigateToSettings()
            ProfileIntent.NavigateToNotifications -> navigateToNotifications()
            ProfileIntent.NavigateToBadges -> navigateToBadges()
            ProfileIntent.NavigateToPennyBox -> navigateToPennyBox()
            ProfileIntent.NavigateToHelp -> navigateToHelp()
            ProfileIntent.NavigateToFeedback -> navigateToFeedback()
            ProfileIntent.NavigateToAboutUs -> navigateToAboutUs()
        }
    }

    private fun unfocusUsername(email: String) {
        screenModelScope.launch {
            val isEmailRegistered = checkIsEmailRegisteredUseCase(email)

            var errorMessage: String? = null

            when (isEmailRegistered) {
                null -> {
                }

                true -> {
                    errorMessage = "Welcome back $email"
                }

                false -> {
                    errorMessage = "New user? A new account will be created for you."
                }
            }

            _uiState.value = _uiState.value.copy(
                errorMessage = errorMessage
            )

        }
    }

    private fun login(email: String, password: String) {
        screenModelScope.launch {
            try {

                val isEmailRegistered = checkIsEmailRegisteredUseCase(email)

                if (isEmailRegistered == null) {
                    _uiState.value = _uiState.value.copy(
                        errorMessage = null
                    )
                    return@launch
                }

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
                            errorMessage = loginResponse.message
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
                        errorMessage = result.message
                    )
                }

            } catch (e: Exception) {
                e.printStackTrace()
                _uiState.value = _uiState.value.copy(
                    errorMessage = "An unknown error occurred during login."
                )
            }
        }
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun fetchProfileStatistics() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        _uiState.value = _uiState.value.copy(
            userUuid = userDataRepository.getUserUuid(),
            email = userDataRepository.getUserEmailOrNull(),
            username = userDataRepository.getUserNameOrNull(),
            // 假设这里获取统计数据
            continuousCheckInDays = 5,
            totalTransactionDays = 30,
            totalTransactionCount = 100
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

    // 导航函数示例
    private fun navigateToSettings() {
        // 执行导航到设置页面的逻辑
    }

    private fun navigateToNotifications() {
        // 执行导航到通知页面的逻辑
    }

    private fun navigateToBadges() {
        // 执行导航到徽章页面的逻辑
    }

    private fun navigateToPennyBox() {
        // 执行导航到 Penny's Box 的逻辑
    }

    private fun navigateToHelp() {
        // 执行导航到使用帮助页面的逻辑
    }

    private fun navigateToFeedback() {
        // 执行导航到意见反馈页面的逻辑
    }

    private fun navigateToAboutUs() {
        // 执行导航到关于我们页面的逻辑
    }
}