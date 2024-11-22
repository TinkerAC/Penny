package app.penny.feature.ledgerDetail

sealed class LedgerDetailIntent {
    data object LoadLedger : LedgerDetailIntent()
    data object DeleteLedger : LedgerDetailIntent()
}