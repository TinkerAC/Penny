// file: src/commonMain/kotlin/app/penny/feature/ledgerDetail/LedgerDetailIntent.kt
package app.penny.feature.ledgerDetail

import app.penny.core.domain.model.LedgerModel

sealed class LedgerDetailIntent {
    data object DeleteLedger : LedgerDetailIntent()
    class LoadLedger(val ledgerUuid: String) : LedgerDetailIntent()
    class ChangeName(val name: String) : LedgerDetailIntent()
    data object SaveLedger : LedgerDetailIntent()
}