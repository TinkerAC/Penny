// NumPadUiState.kt

package app.penny.presentation.ui.components.numPad

data class NumPadUiState(
    val operand1: String = "0.00",
    val operator: String = "",
    val operand2: String = "",
    val amountText: String = "0.00",
    val remarkText: String = "",
    val doneButtonState: DoneButtonState = DoneButtonState.CANCEL,
    val doneButtonText: String = "取消"
)


enum class DoneButtonState {
    CANCEL,
    COMPLETE,
    EQUAL
}