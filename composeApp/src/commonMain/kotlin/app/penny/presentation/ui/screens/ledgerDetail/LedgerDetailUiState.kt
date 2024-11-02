package app.penny.presentation.ui.screens.ledgerDetail

import app.penny.domain.model.LedgerModel

data class LedgerDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val ledger: LedgerModel,
)