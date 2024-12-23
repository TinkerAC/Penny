package app.penny.core.domain.exception

sealed class LedgerException(message: String) : Throwable(message) {
    data object CanNotDeleteDefaultLedger : LedgerException("Can not delete default ledger")

}
