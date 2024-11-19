package app.penny.presentation.ui.screens.newLedger

sealed class NewLedgerUiEvent {
    data class ShowSnackbar(val message: String) : NewLedgerUiEvent()
    data object NavigateBack : NewLedgerUiEvent()
}
