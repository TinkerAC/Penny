package app.penny.feature.profile

import app.penny.core.data.repository.UserDataRepository
import app.penny.core.domain.usecase.CheckIsUsernameValidUseCase
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
    private val checkIsUsernameValidUseCase: CheckIsUsernameValidUseCase
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
            is ProfileIntent.UnfocusUsername -> unfocusUsername(intent.username)
        }
    }

    private fun unfocusUsername(username: String) {
        //check if the username is valid
        screenModelScope.launch {
            val isUsernameValid = checkIsUsernameValidUseCase(username)
            _uiState.value = _uiState.value.copy(
                isUsernameValid = isUsernameValid
            )
        }

    }

    private fun login(username: String, password: String) {

        screenModelScope.launch {
            //login
            val userUuid = userDataRepository.login(username, password)
            _uiState.value = _uiState.value.copy(
                userUuid = userUuid
            )


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
        //pop the login modal
        _uiState.value = _uiState.value.copy(
            loggingModalVisible = true
        )
    }


}
