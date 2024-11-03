// NumPadViewModel.kt

package app.penny.presentation.ui.components.numPad

import cafe.adriel.voyager.core.model.ScreenModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow


class NumPadViewModel : ScreenModel {

    private val _uiState = MutableStateFlow(NumPadUiState())
    val uiState: StateFlow<NumPadUiState> = _uiState.asStateFlow()

    private val operators = setOf("+", "-", "×", "÷")


    private val decimalMode =
        DecimalMode(20, roundingMode = RoundingMode.ROUND_HALF_CEILING, scale = 2)


    fun onNumPadButtonClicked(numPadButton: NumPadButton) {
        when (numPadButton) {
            is NumPadButton.Number -> handleNumber(numPadButton)
            NumPadButton.Decimal -> handleDecimal()
            NumPadButton.Backspace -> handleBackspace()
            is NumPadButton.Operator -> handleOperator(numPadButton)
            is NumPadButton.Function.AddAnotherTransaction -> handleAddAnotherTransaction()
            else -> Unit
        }
        updateDoneButtonState()
    }

    fun onDoneButtonClicked() {
        when (_uiState.value.doneButtonState) {
            DoneButtonState.CANCEL -> {
                // 从导航器弹出（暂不实现）
            }

            DoneButtonState.COMPLETE -> {
                if (_uiState.value.amountText == "0.00"||_uiState.value.amountText == "错误") {
                    return
                }
                // 这里只进行计算
//                calculate()
                // 插入记录逻辑（暂不实现）
                println("插入记录逻辑（暂不实现）")

                // 从导航器弹出（暂不实现）
            }

            DoneButtonState.EQUAL -> {
                // 计算结果并更新 amountText
                calculate()
                _uiState.value = _uiState.value.copy(doneButtonState = DoneButtonState.COMPLETE)
            }
        }
        updateDoneButtonState()
    }

    fun onRemarkChanged(newRemark: String) {
        _uiState.value = _uiState.value.copy(remarkText = newRemark)
    }

    private fun handleNumber(numPadButton: NumPadButton.Number) {
        val currentText = _uiState.value.amountText


        val newText = if (currentText.contains("错误") || currentText == "0.00") {
            numPadButton.text
        } else {
            currentText + numPadButton.text
        }
        _uiState.value = _uiState.value.copy(amountText = newText)
    }

    private fun handleDecimal() {
        val currentText = _uiState.value.amountText
        if (!currentText.contains(".")) {
            _uiState.value = _uiState.value.copy(amountText = "$currentText.")
        }
    }

    private fun handleBackspace() {
        val currentText = _uiState.value.amountText
        val newText =
            if (currentText.length <= 1 || currentText == "错误" || currentText == "0.00") {
                "0.00"
            } else {
                currentText.dropLast(1)
            }
        _uiState.value = _uiState.value.copy(amountText = newText)
    }

    private fun handleOperator(numPadButton: NumPadButton.Operator) {
        val currentText = _uiState.value.amountText

        if (currentText.contains("错误")) {
            return
        }

        if (operators.any { currentText.endsWith(it) }) {
            _uiState.value =
                _uiState.value.copy(amountText = currentText.dropLast(1) + numPadButton.text)
            return
        }

        if (currentText.isNotEmpty() && !operators.contains(currentText.last().toString())) {
            _uiState.value = _uiState.value.copy(amountText = currentText + numPadButton.text)
        }
    }

    private fun handleAddAnotherTransaction() {
        // 插入记录逻辑（暂不实现）
        // 恢复到初始状态
        _uiState.value = NumPadUiState()
    }

    private fun calculate() {
        val expression = _uiState.value.amountText
        try {

            val result = evaluateExpression(expression)
            _uiState.value = _uiState.value.copy(amountText = result.toPlainString())
        } catch (e: Exception) {
            println(e)
            _uiState.value = _uiState.value.copy(amountText = "错误")
        }
    }


    /**
     * 解析表达式并提取操作数和操作符。
     *
     * @param expression 要解析的表达式字符串。
     * @return 如果表达式有效，返回一个包含第一个操作数、操作符和第二个操作数的 Triple。
     *         如果表达式无效，返回 null。
     */
    private fun parseExpression(expression: String): Triple<String, String, String>? {
        // 定义正则表达式：
        // ^\s*                : 可选的前导空白
        // (-?\d+(?:\.\d+)?)   : 第一个操作数，可能带有 - 符号，整数或小数
        // \s*                 : 可选的空白
        // ([+\-×÷])           : 操作符，捕获 +, -, ×, ÷
        // \s*                 : 可选的空白
        // (\d+(?:\.\d+)?)     : 第二个操作数，不带符号，整数或小数
        // \s*$                : 可选的尾随空白
        val regexFull = """^\s*(-?\d+(?:\.\d+)?)\s*([+\-×÷])\s*(\d+(?:\.\d+)?)\s*$""".toRegex()

        // 定义另一个正则表达式，用于仅包含一个操作数和操作符
        // ^\s*                : 可选的前导空白
        // (-?\d+(?:\.\d+)?)   : 第一个操作数，可能带有 - 符号，整数或小数
        // \s*                 : 可选的空白
        // ([+\-×÷])           : 操作符，捕获 +, -, ×, ÷
        // \s*$                : 可选的尾随空白
        val regexPartial = """^\s*(-?\d+(?:\.\d+)?)\s*([+\-×÷])\s*$""".toRegex()

        // 尝试匹配完整的表达式（两个操作数和一个操作符）
        val matchFull = regexFull.find(expression)
        if (matchFull != null) {
            val (operand1, operator, operand2) = matchFull.destructured
            return Triple(operand1, operator, operand2)
        }

        // 如果完整匹配失败，尝试部分匹配（只有一个操作数和一个操作符，默认第二个操作数为 "0"）
        val matchPartial = regexPartial.find(expression)
        if (matchPartial != null) {
            val (operand1, operator) = matchPartial.destructured
            // 默认第二个操作数为 "0"
            return Triple(operand1, operator, "0")
        }

        // 如果都不匹配，返回 null 表示无效的表达式
        return null
    }


    private fun evaluateExpression(expression: String): BigDecimal {

        val (opr1, opt, opr2) = parseExpression(expression)
            ?: throw IllegalArgumentException("无效的表达式：$expression")

        val operand1 = try {
            BigDecimal.parseString(opr1)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }

        val operand2 = try {
            BigDecimal.parseString(opr2)
        } catch (e: Exception) {
            BigDecimal.ZERO
        }

        val result = when (opt) {
            "+" -> operand1 + operand2
            "-" -> operand1 - operand2
            "×" -> operand1.times(operand2)
            "÷" -> {
                if (operand2 == BigDecimal.ZERO) throw IllegalArgumentException("除数不能为 0")
                operand1.divide(operand2, decimalMode)
            }
            else -> throw IllegalArgumentException("未知的运算符${opt}")
        }
        println(
            "processing ${operand1.toPlainString()} ${
                when (opt) {
                    "×" -> "times"
                    "÷" -> "divide"
                    "+" -> "plus"
                    "-" -> "minus"
                    else -> "unknown($opt)"
                }
            } $operand2 = $result"
        )

        return result.divide(BigDecimal.ONE, decimalMode)
    }

    private fun updateDoneButtonState() {
        val amountText = _uiState.value.amountText

        val isInitialState = amountText == "0.00" || amountText == "0"
        val hasOperator = operators.any { amountText.contains(it) }

        val newState = when {
            isInitialState -> DoneButtonState.CANCEL
            hasOperator -> DoneButtonState.EQUAL
            else -> DoneButtonState.COMPLETE
        }

        val newText = when (newState) {
            DoneButtonState.CANCEL -> "取消"
            DoneButtonState.COMPLETE -> "完成"
            DoneButtonState.EQUAL -> "="
        }

        _uiState.value = _uiState.value.copy(
            doneButtonState = newState,
            doneButtonText = newText
        )
    }
}
