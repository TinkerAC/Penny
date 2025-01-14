// NumPadButton.kt

package app.penny.feature.newTransaction.component

sealed class NumPadButton(
    val text: String
) {
    // number buttons
    data class Number(val numberText: String) : NumPadButton(numberText)

    // operator buttons
    data class Operator(val operatorText: String) : NumPadButton(operatorText)

    // functional buttons
    sealed class Function(text: String) : NumPadButton(text) {
        data object Done : Function("Done")
        data object AddAnotherTransaction : Function("")
    }

    // other buttons
    data object Decimal : NumPadButton(".")
    data object Backspace : NumPadButton("DEL")
}

enum class DoneButtonState(
    val displayText: String
) {
    CANCEL("Cancel"),
    COMPLETE("Done"),
    EQUAL("=")
}