package app.penny.presentation.viewmodel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.lifecycle.ViewModel
import app.penny.presentation.DashboardUiState
import kotlinx.coroutines.flow.asStateFlow
import app.penny.presentation.TransactionUiState
class DashboardViewModel: ViewModel() {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

}