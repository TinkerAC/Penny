package app.penny.feature.ledgerDetail

import app.penny.core.domain.model.LedgerModel

data class LedgerDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val ledger: LedgerModel,
)