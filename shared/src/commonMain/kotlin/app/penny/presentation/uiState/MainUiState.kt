package app.penny.presentation.uiState

import app.penny.core.domain.model.LedgerModel

data class MainUiState(
    val ledgers: List<LedgerModel> = emptyList(),
    val selectedLedgerId: Long? = null,
    val firstTime: Boolean = false
)