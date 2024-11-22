package app.penny.feature.myLedger

import app.penny.core.domain.model.LedgerModel

data class MyLedgerUiState(

    val isLoading: Boolean = false,
    val ledgers: List<LedgerModel> = emptyList<LedgerModel>()

) {


}