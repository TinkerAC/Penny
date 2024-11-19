package app.penny.presentation.ui.screens.ledgerDetail

sealed class LedgerDetailIntent {
    data object LoadLedger : LedgerDetailIntent()
    data object DeleteLedger : LedgerDetailIntent()
}