package app.penny.presentation.ui.screens.profile

import app.penny.data.repository.UserDataRepository
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

class ProfileViewModel(
    private val userDataRepository: UserDataRepository
) : ScreenModel {

    @OptIn(ExperimentalUuidApi::class)
    private val _uiState = MutableStateFlow(ProfileUiState())

    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {
        screenModelScope.launch {
            fetchProfileStatistics()
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
}