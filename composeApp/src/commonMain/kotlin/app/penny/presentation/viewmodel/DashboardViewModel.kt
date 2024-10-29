package app.penny.presentation.viewmodel

import app.penny.presentation.DashboardUiState
import cafe.adriel.voyager.core.model.ScreenModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DashboardViewModel : ScreenModel {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()



    fun insertTransaction() {
        _uiState.value = DashboardUiState(isLoading = true)
    }

}