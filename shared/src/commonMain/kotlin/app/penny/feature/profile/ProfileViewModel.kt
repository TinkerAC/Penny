package app.penny.feature.profile

import app.penny.core.data.repository.AuthRepository
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.StatisticRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.exception.LoginException
import app.penny.core.domain.exception.RegisterException
import app.penny.core.domain.usecase.LoginUseCase
import app.penny.core.domain.usecase.RegisterUseCase
import app.penny.presentation.utils.generateGravatarUrl
import app.penny.servershared.dto.responseDto.LoginResponse
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class ProfileViewModel(
    private val userDataRepository: UserDataRepository,
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val statisticRepository: StatisticRepository,
    private val transactionRepository: TransactionRepository,
    private val ledgerRepository: LedgerRepository


) : ScreenModel {

    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<Boolean>(replay = 0)
    val eventFlow = _eventFlow.asSharedFlow()


    init {
        screenModelScope.launch {
            fetchProfileStatistics()
        }
    }


    fun refreshData() {
        screenModelScope.launch {
            fetchProfileStatistics()
        }
    }


    fun handleIntent(intent: ProfileIntent) {
        when (intent) {
            ProfileIntent.TryLogin -> showLoginModal()
            is ProfileIntent.Login -> attemptLogin(intent.email, intent.password)
            is ProfileIntent.Register -> attemptRegister(
                intent.email, intent.password, intent.confirmPassword
            )

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

    private fun checkEmailStatus(email: String?) {

        if (email.isNullOrBlank()) {
            Logger.d("Email not checked for $email")
            return
        }

        screenModelScope.launch {
            val errorMessage: String? = null

            _uiState.value = _uiState.value.copy(errorMessage = errorMessage)
        }
    }

    private fun attemptLogin(email: String, password: String) {
        screenModelScope.launch {
            val loginResponse: LoginResponse
            try {
                loginResponse = loginUseCase(email, password)
            } catch (e: LoginException) {
                _uiState.value = _uiState.value.copy(
                    errorMessage = when (e) {
                        LoginException.NetworkException -> "Network error, please try again later"
                        LoginException.ServerException -> "Server error, please try again later"
                        LoginException.InvalidCredentialsException -> "Invalid email or password"
                        is LoginException.UnknownException -> "Unknown error occurred"
                    }
                )
                return@launch
            }

            if (loginResponse.success) {
                _uiState.value = _uiState.value.copy(
                    loggingModalVisible = false,
                    username = loginResponse.userDto?.username ?: "",
                    errorMessage = null,
                    isLoggedIn = true
                )
                fetchProfileStatistics()

                //check if user has any ledger ,if not create one
                val user = userDataRepository.getUser()
                val ledger = ledgerRepository.findByUserUuid(user.uuid)
                if (ledger.isEmpty()) {
                    _eventFlow.emit(true)
                }
            } else {
                _uiState.value = _uiState.value.copy(
                    errorMessage = loginResponse.message
                )
            }
        }
    }

    private fun attemptRegister(email: String, password: String, confirmPassword: String) {
        screenModelScope.launch {
            // 尝试调用usecase进行注册

            try {


                val registerResponse =
                    registerUseCase(email, password, confirmPassword, Uuid.random().toString())
                if (registerResponse.success) {
                    // 注册成功
                    _uiState.value = _uiState.value.copy(
                        errorMessage = "Register success, please login", modalInLoginMode = true
                    )

                } else {
                    // 理论上这里不会被执行，因为不成功的情况在 usecase 中已抛异常
                    _uiState.value = _uiState.value.copy(
                        errorMessage = registerResponse.message
                    )
                }
            } catch (e: RegisterException) {
                val errorMsg = when (e) {
                    RegisterException.EmailAlreadyRegisteredException -> "This email has been registered, please login"
                    RegisterException.NetworkException -> "Network error, please try again later"
                    RegisterException.ServerException -> "Server error, please try again later"
                    is RegisterException.UnknownException -> "Unknown error occurred"
                    RegisterException.PasswordNotMatchException -> "Password not match"
                }
                _uiState.value = _uiState.value.copy(errorMessage = errorMsg)
            }
        }
    }

    private fun toggleModalMode() {
        _uiState.value = _uiState.value.copy(
            modalInLoginMode = !_uiState.value.modalInLoginMode, errorMessage = null
        )
    }

    private fun showLoginModal() {
        _uiState.value = _uiState.value.copy(
            loggingModalVisible = true, errorMessage = null
        )
    }


    private fun dismissLoginModal() {
        _uiState.value = _uiState.value.copy(
            loggingModalVisible = false, errorMessage = null
        )
    }

    private suspend fun fetchProfileStatistics() {
        _uiState.value = _uiState.value.copy(isLoading = true)

        val user = userDataRepository.getUser()
        _uiState.value = _uiState.value.copy(
            user = user,
            isLoggedIn = authRepository.hasLoggedIn(),
            email = userDataRepository.getUserEmailOrNull(),
            username = userDataRepository.getUserNameOrNull(),
            totalTransactionDateSpan = statisticRepository.getTransactionDateSpanDays(user),
            ledgerCount = ledgerRepository.countByUser(user),
            totalTransactionCount = statisticRepository.getTotalTransactionCountByUser(user),
            isLoading = false,
            userAvatarUrl = user.email?.let { generateGravatarUrl(it) } ?: ""
        )
    }
}