package app.penny.feature.profile

import app.penny.core.data.repository.AuthRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.usecase.LoginUseCase
import app.penny.core.domain.usecase.RegisterUseCase
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class ProfileViewModel(
    private val userDataRepository: UserDataRepository,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository

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
            ProfileIntent.TryLogin -> showLoginModal()
            is ProfileIntent.Login -> attemptLogin(intent.email, intent.password)
            is ProfileIntent.Register -> attemptRegister(intent.email, intent.password)
            is ProfileIntent.UnfocusEmail -> checkEmailStatus(intent.email)
            ProfileIntent.DismissLoginModal -> dismissLoginModal()
            ProfileIntent.ToggleModalMode -> toggleModalMode()
            is ProfileIntent.InputEmail -> {
                _uiState.value = _uiState.value.copy(email = intent.email)
            }
        }
    }

    private suspend fun checkEmailAvailability(email: String): Boolean? {
        // 返回值：true已注册，false未注册，null未知（出错）
        return authRepository.checkIsEmailRegistered(email)
    }

    private fun checkEmailStatus(email: String) {
        screenModelScope.launch {
            val isEmailRegistered = checkEmailAvailability(email)
            val localUser = userRepository.findByEmailIsNull()

            val errorMessage: String? = when {
                isEmailRegistered == null -> {
                    // 无法确定该邮箱是否已注册
                    // TODO: 在此根据需求添加错误提示或逻辑
                    null
                }
                localUser == null -> {
                    // 无本地匿名用户
                    if (isEmailRegistered) "此邮箱已注册，请登录" else "此邮箱未注册，将创建新账户"
                }
                else -> {
                    // 有本地匿名用户（意味着绑定）
                    if (isEmailRegistered) "此邮箱已注册，将尝试绑定" else "此邮箱未注册，可将本地数据绑定到新账户"
                }
            }

            _uiState.value = _uiState.value.copy(errorMessage = errorMessage)
        }
    }

    private fun attemptLogin(email: String, password: String) {
        screenModelScope.launch {
            val isEmailRegistered = checkEmailAvailability(email)

            if (isEmailRegistered == null) {
                // 无法确定邮箱状态
                // TODO: 在此添加错误逻辑处理
                _uiState.value = _uiState.value.copy(errorMessage = "网络错误，请稍后再试")
                return@launch
            }

            if (!isEmailRegistered) {
                // 邮箱未注册，无法登录
                _uiState.value = _uiState.value.copy(errorMessage = "邮箱未注册，请先注册")
                return@launch
            }

            // 尝试登录
            val loginResponse = loginUseCase(email, password)
            if (loginResponse.success) {
                _uiState.value = _uiState.value.copy(
                    loggingModalVisible = false,
                    username = loginResponse.userDto?.username ?: "",
                    errorMessage = null,
                    isLoggedIn = true
                )
                fetchProfileStatistics()
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = loginResponse.message
                )
            }
        }
    }

    private fun attemptRegister(email: String, password: String) {
        screenModelScope.launch {
            val isEmailRegistered = checkEmailAvailability(email)
            if (isEmailRegistered == null) {
                // 无法确定邮箱状态
                // TODO: 在此添加错误逻辑处理
                _uiState.value = _uiState.value.copy(errorMessage = "网络错误，请稍后再试")
                return@launch
            }

            if (isEmailRegistered) {
                // 邮箱已注册
                _uiState.value = _uiState.value.copy(errorMessage = "此邮箱已注册，请直接登录")
                return@launch
            }

            // 邮箱未注册，尝试注册
            val registerResponse = registerUseCase(email, password)
            if (registerResponse.success) {
                // 注册成功后可选择自动登录或提示用户登录
                // TODO: 若需要注册后自动登录可在此调用 attemptLogin(...)
                _uiState.value = _uiState.value.copy(
                    errorMessage = "注册成功，请登录",
                    modalInLoginMode = true
                )
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = registerResponse.message ?: "注册过程中出现未知错误"
                )
            }
        }
    }

    private fun toggleModalMode() {
        _uiState.value = _uiState.value.copy(
            modalInLoginMode = !_uiState.value.modalInLoginMode,
            errorMessage = null
        )
    }

    private fun showLoginModal() {
        _uiState.value = _uiState.value.copy(
            loggingModalVisible = true,
            errorMessage = null
        )
    }

    private fun dismissLoginModal() {
        _uiState.value = _uiState.value.copy(
            loggingModalVisible = false,
            errorMessage = null
        )
    }

    @OptIn(ExperimentalUuidApi::class)
    private suspend fun fetchProfileStatistics() {
        _uiState.value = _uiState.value.copy(isLoading = true)
        _uiState.value = _uiState.value.copy(
            userUuid = userDataRepository.getUserUuid(),
            email = userDataRepository.getUserEmailOrNull(),
            username = userDataRepository.getUserNameOrNull(),
            continuousCheckInDays = 5,   // TODO: 从业务逻辑中获取实际数据
            totalTransactionDays = 30,   // TODO: 从业务逻辑中获取实际数据
            totalTransactionCount = 100, // TODO: 从业务逻辑中获取实际数据
            isLoading = false
        )
    }
}