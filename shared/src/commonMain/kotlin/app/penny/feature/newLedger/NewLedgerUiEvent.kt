package app.penny.feature.newLedger

sealed class NewLedgerUiEvent {
    data class ShowSnackbar(val message: String) : NewLedgerUiEvent()
    data object NavigateBack : NewLedgerUiEvent()
}
