// shared/src/commonMain/kotlin/app/penny/feature/newLedger/intents/NewLedgerIntent.kt
package app.penny.feature.newLedger

import app.penny.core.domain.enumerate.Currency
import app.penny.core.domain.enumerate.LedgerCover

sealed class NewLedgerIntent {
    object OpenCurrencySelectorModal : NewLedgerIntent()
    object CloseCurrencySelectorModal : NewLedgerIntent()
    object ConfirmCreateLedger : NewLedgerIntent()
    data class SelectCover(val cover: LedgerCover) : NewLedgerIntent()
    data class SetLedgerName(val name: String) : NewLedgerIntent()
    data class SelectCurrency(val currency: Currency) : NewLedgerIntent()
}
