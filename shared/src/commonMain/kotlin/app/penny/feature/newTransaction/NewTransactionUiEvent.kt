// file: shared/src/commonMain/kotlin/app/penny/feature/newTransaction/NewTransactionUiEvent.kt
package app.penny.feature.newTransaction

sealed class NewTransactionUiEvent {
    data class ShowSnackBar(val message: String) : NewTransactionUiEvent()
    data object NavigateBack : NewTransactionUiEvent()
}