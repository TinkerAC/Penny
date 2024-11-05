package app.penny.presentation.ui.screens.newLedger

import app.penny.domain.enum.Currency
import app.penny.domain.enum.LedgerCover

sealed class NewLedgerIntent {
    data object OpenCurrencySelectorModal : NewLedgerIntent()
    data object CloseCurrencySelectorModal : NewLedgerIntent()
    data object ConfirmCreateLedger : NewLedgerIntent()
    data class SelectCover(val cover: LedgerCover) : NewLedgerIntent()
    data class SetLedgerName(val name: String) : NewLedgerIntent()
    data class SelectCurrency(val currency: Currency) : NewLedgerIntent()


}