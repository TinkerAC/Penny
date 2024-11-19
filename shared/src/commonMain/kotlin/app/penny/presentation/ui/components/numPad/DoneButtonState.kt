// DoneButtonState.kt

package app.penny.presentation.ui.components.numPad


enum class DoneButtonState(
    val displayText: String
) {
    CANCEL("Cancel"),
    COMPLETE("Done"),
    EQUAL("=")
}