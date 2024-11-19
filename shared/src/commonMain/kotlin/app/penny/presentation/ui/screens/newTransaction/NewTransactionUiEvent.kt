package app.penny.presentation.ui.screens.newTransaction

sealed class NewTransactionUiEvent {
    data class ShowSnackBar(val message: String) : NewTransactionUiEvent()
    data object NavigateBack : NewTransactionUiEvent()
}