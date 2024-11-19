package app.penny.presentation.ui.screens.myLedger

import app.penny.domain.model.LedgerModel

data class MyLedgerUiState(

    val isLoading: Boolean = false,
    val ledgers: List<LedgerModel> = emptyList<LedgerModel>()

) {


}