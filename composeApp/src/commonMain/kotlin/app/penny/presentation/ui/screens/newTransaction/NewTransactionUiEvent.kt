package app.penny.presentation.ui.screens.newTransaction

sealed class NewTransactionUiEvent {
    data class ShowSnackbar(val message: String) : NewTransactionUiEvent()
    data object NavigateBack : NewTransactionUiEvent()
}