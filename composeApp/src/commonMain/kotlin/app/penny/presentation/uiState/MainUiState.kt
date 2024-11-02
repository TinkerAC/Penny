package app.penny.presentation.uiState

import app.penny.domain.model.LedgerModel

data class MainUiState(
    val ledgers: List<LedgerModel> = emptyList(),
    val selectedLedgerId: Long? = null
)