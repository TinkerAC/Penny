// NumPadButton.kt

package app.penny.presentation.ui.components.numPad

sealed class NumPadButton(
    val text: String
) {
    // 数字按钮
    data class Number(val numberText: String) : NumPadButton(numberText)

    // 运算符按钮
    data class Operator(val operatorText: String) : NumPadButton(operatorText)

    // 功能按钮
    sealed class Function(text: String) : NumPadButton(text) {
        object Done : Function("Done")
        object AddAnotherTransaction : Function("Add Another")
    }

    // 其他按钮
    object Decimal : NumPadButton(".")
    object Backspace : NumPadButton("DEL")
}
