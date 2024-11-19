package app.penny.presentation.ui.screens.profile

import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ProfileViewModel : ScreenModel {

    private val _uiState = MutableStateFlow(ProfileUiState())

    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    init {


    }


    private fun fetchProfileStatistics() {
        _uiState.value = _uiState.value.copy(isLoading = true)




    }
}