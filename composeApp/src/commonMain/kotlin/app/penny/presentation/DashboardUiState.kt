package app.penny.presentation

import app.penny.domain.model.Transaction

data class DashboardUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)
