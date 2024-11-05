// file: composeApp/src/commonMain/kotlin/app/penny/presentation/ui/screens/newTransaction/NewTransactionViewModel.kt
package app.penny.presentation.ui.screens.newTransaction

import app.penny.data.model.TransactionType
import app.penny.data.repository.TransactionRepository
import app.penny.domain.enum.ExpenseCategory
import app.penny.domain.enum.IncomeCategory
import app.penny.domain.model.TransactionModel
import app.penny.presentation.ui.components.numPad.DoneButtonState
import app.penny.presentation.ui.components.numPad.NumPadButton
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class NewTransactionViewModel(
    private val transactionRepository: TransactionRepository
) : ScreenModel {

    private val _uiState = MutableStateFlow(NewTransactionUiState())
    val uiState: StateFlow<NewTransactionUiState> = _uiState.asStateFlow()

    private val operators = setOf("+", "-", "×", "÷")

    private val decimalMode =
        DecimalMode(20, roundingMode = RoundingMode.ROUND_HALF_CEILING, scale = 2)

    // 处理用户意图
    fun handleIntent(intent: NewTransactionIntent) {
        when (intent) {
            is NewTransactionIntent.InsertTransaction -> insertTransaction()
            is NewTransactionIntent.SelectTab -> selectTab(intent.tab)
            is NewTransactionIntent.SetAmount -> setAmount(intent.amount)
            is NewTransactionIntent.SelectIncomeCategory -> selectIncomeCategory(intent.category)
            is NewTransactionIntent.SelectExpenseCategory -> selectExpenseCategory(intent.category)
            is NewTransactionIntent.SetRemark -> setRemark(intent.remark)
        }
    }

    private fun insertTransaction() {
        screenModelScope.launch {
            transactionRepository.insertTransaction(
                TransactionModel(
                    ledgerId = 1,
                    transactionDate = Clock.System.now().toEpochMilliseconds(),
                    categoryId = 1,
                    transactionType = TransactionType.EXPENSE,
                    amount = BigDecimal.ONE,
                    currencyCode = "CNY",
                    content = "sample",
                    screenshotUri = "",
                    note = "note"
                )
            )
            _uiState.value = _uiState.value.copy(transactionCompleted = true)
        }
    }

    private fun selectTab(tab: NewTransactionTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
    }

    private fun setAmount(amount: BigDecimal) {
        _uiState.value = _uiState.value.copy(amount = amount)
    }

    private fun selectIncomeCategory(category: IncomeCategory) {
        _uiState.value = _uiState.value.copy(selectedIncomeCategory = category)
    }

    private fun selectExpenseCategory(category: ExpenseCategory) {
        _uiState.value = _uiState.value.copy(selectedExpenseCategory = category)
    }

    private fun setRemark(remark: String) {
        _uiState.value = _uiState.value.copy(remark = remark)
    }

    // 以下是从 NumPadViewModel 移植过来的方法

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
                // 处理取消逻辑
            }
            DoneButtonState.COMPLETE -> {
                if (_uiState.value.amountText == "0.00" || _uiState.value.amountText == "错误") {
                    // 无效的金额
                    return
                }
                _uiState.value = _uiState.value.copy(amount = BigDecimal.parseString(_uiState.value.amountText))
                insertTransaction()
            }
            DoneButtonState.EQUAL -> {
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
        val number = numPadButton.text
        if (_uiState.value.operator.isEmpty()) {
            var operand1 = _uiState.value.operand1
            if (operand1 == "0.00" || operand1 == "0" || operand1 == "-0.00" || operand1 == "-0" || operand1 == "错误") {
                operand1 = if (operand1.startsWith("-")) "-$number" else number
            } else {
                operand1 += number
            }
            _uiState.value = _uiState.value.copy(operand1 = operand1)
        } else {
            var operand2 = _uiState.value.operand2
            if (operand2 == "0.00" || operand2 == "0") {
                operand2 = number
            } else {
                operand2 += number
            }
            _uiState.value = _uiState.value.copy(operand2 = operand2)
        }
        updateAmountText()
    }

    private fun handleDecimal() {
        if (_uiState.value.operator.isEmpty()) {
            var operand1 = _uiState.value.operand1
            if (!operand1.contains(".")) {
                if (operand1.isEmpty() || operand1 == "-") {
                    operand1 += "0."
                } else {
                    operand1 += "."
                }
                _uiState.value = _uiState.value.copy(operand1 = operand1)
            }
        } else {
            var operand2 = _uiState.value.operand2
            if (!operand2.contains(".")) {
                if (operand2.isEmpty()) {
                    operand2 += "0."
                } else {
                    operand2 += "."
                }
                _uiState.value = _uiState.value.copy(operand2 = operand2)
            }
        }
        updateAmountText()
    }

    private fun handleBackspace() {
        if (_uiState.value.operand2.isNotEmpty()) {
            var operand2 = _uiState.value.operand2
            operand2 = operand2.dropLast(1)
            _uiState.value = _uiState.value.copy(operand2 = operand2)
        } else if (_uiState.value.operator.isNotEmpty()) {
            _uiState.value = _uiState.value.copy(operator = "")
        } else {
            var operand1 = _uiState.value.operand1
            operand1 = operand1.dropLast(1)
            if (operand1.isEmpty() || operand1 == "-" || operand1 == "-0") {
                operand1 = "0.00"
            }
            _uiState.value = _uiState.value.copy(operand1 = operand1)
        }
        updateAmountText()
    }

    private fun handleOperator(numPadButton: NumPadButton.Operator) {
        val operatorText = numPadButton.text

        if (operatorText == "-" && _uiState.value.operator.isEmpty() && (_uiState.value.operand1 == "0.00" || _uiState.value.operand1.isEmpty())) {
            // 处理负号作为 operand1 的符号
            _uiState.value = _uiState.value.copy(operand1 = "-")
        } else if (_uiState.value.operator.isEmpty()) {
            // 设置操作符
            _uiState.value = _uiState.value.copy(operator = operatorText)
        } else if (_uiState.value.operand2.isEmpty()) {
            // 更改操作符
            _uiState.value = _uiState.value.copy(operator = operatorText)
        } else {
            // 计算结果并设置新的操作符
            calculate()
            _uiState.value = _uiState.value.copy(operator = operatorText, operand2 = "")
        }
        updateAmountText()
    }

    private fun handleAddAnotherTransaction() {
        // 处理添加新交易逻辑（暂不实现）
        _uiState.value = NewTransactionUiState()
    }

    private fun calculate() {
        val operand1Str = _uiState.value.operand1
        val operator = _uiState.value.operator
        val operand2Str = _uiState.value.operand2

        if (operator.isEmpty() || operand2Str.isEmpty()) {
            // 无需计算
            return
        }

        try {
            val operand1 = BigDecimal.parseString(operand1Str)
            val operand2 = BigDecimal.parseString(operand2Str)

            var result = when (operator) {
                "+" -> operand1 + operand2
                "-" -> operand1 - operand2
                "×" -> operand1 * operand2
                "÷" -> {
                    if (operand2 == BigDecimal.ZERO) throw IllegalArgumentException("除数不能为 0")
                    operand1.divide(operand2, decimalMode)
                }
                else -> throw IllegalArgumentException("未知的运算符 $operator")
            }

            result = result.divide(BigDecimal.ONE, decimalMode)

            _uiState.value = _uiState.value.copy(
                operand1 = result.toPlainString(),
                operator = "",
                operand2 = ""
            )
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                operand1 = "错误",
                operator = "",
                operand2 = ""
            )
        }
        updateAmountText()
    }

    private fun updateDoneButtonState() {
        val operand1 = _uiState.value.operand1
        val operator = _uiState.value.operator
        val operand2 = _uiState.value.operand2

        val isInitialState =
            (operand1 == "0.00" || operand1.isEmpty()) && operator.isEmpty() && operand2.isEmpty()
        val hasOperatorAndOperand2 = operator.isNotEmpty() && operand2.isNotEmpty()
        val hasError = operand1 == "错误"

        val newState = when {
            isInitialState || hasError -> DoneButtonState.CANCEL
            hasOperatorAndOperand2 -> DoneButtonState.EQUAL
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

    private fun updateAmountText() {
        val amountText = _uiState.value.operand1 + _uiState.value.operator + _uiState.value.operand2
        _uiState.value = _uiState.value.copy(amountText = amountText)
    }

    fun resetTransactionCompleted() {
        _uiState.value = _uiState.value.copy(transactionCompleted = false)
    }
}
