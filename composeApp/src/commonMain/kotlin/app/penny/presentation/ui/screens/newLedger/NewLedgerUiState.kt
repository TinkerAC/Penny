package app.penny.presentation.ui.screens.newLedger

import app.penny.domain.enum.Currency
import app.penny.domain.enum.LedgerCover

data class NewLedgerUiState(
    val ledgerName: String = "",
    val isLoading: Boolean = false,
    val ledgerCover: LedgerCover = LedgerCover.DEFAULT,
    val ledgerDescription: String = "",
    val currency: Currency = Currency.USD,
    val currencySelectorModalVisible: Boolean = false,
    val errorMessage: String? = null,
    val ledgerCreateSuccess: Boolean? = null
) {

}