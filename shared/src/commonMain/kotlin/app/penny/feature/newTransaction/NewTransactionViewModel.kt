// file: composeApp/src/commonMain/kotlin/app/penny/presentation/ui/screens/newTransaction/NewTransactionViewModel.kt
package app.penny.feature.newTransaction

import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.Currency
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.TransactionModel
import app.penny.presentation.ui.components.numPad.DoneButtonState
import app.penny.presentation.ui.components.numPad.NumPadButton
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import co.touchlab.kermit.Logger
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock

class NewTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val ledgerRepository: LedgerRepository
) : ScreenModel {


    private val _uiState = MutableStateFlow(NewTransactionUiState())
    val uiState: StateFlow<NewTransactionUiState> = _uiState.asStateFlow()

    private val _eventFlow = MutableSharedFlow<NewTransactionUiEvent>(
        replay = 0
    )
    val eventFlow = _eventFlow.asSharedFlow()


    private val decimalMode =
        DecimalMode(20, roundingMode = RoundingMode.ROUND_HALF_CEILING, scale = 2)


    init {
        fetchLedgers()
        selectParentCategory(
            when (_uiState.value.selectedTab) {
                NewTransactionTab.INCOME -> Category.OTHER_INCOME
                NewTransactionTab.EXPENSE -> Category.MISCELLANEOUS
            }
        )
    }


    fun handleIntent(intent: NewTransactionIntent) {
        when (intent) {
            is NewTransactionIntent.SelectTab -> selectTab(intent.tab)
            is NewTransactionIntent.SetAmount -> setAmount(intent.amount)
            is NewTransactionIntent.SelectCategory -> selectCategory(intent.category)
            is NewTransactionIntent.SetRemark -> setRemark(intent.remark)
            is NewTransactionIntent.ToggleLedgerDropdown -> toggleLedgerDropdown()
            is NewTransactionIntent.SelectLedger -> selectLedger(intent.ledger)
            is NewTransactionIntent.SelectParentCategory -> selectParentCategory(intent.category)
            is NewTransactionIntent.SelectSubCategory -> selectSubCategory(intent.category)
        }
    }

    private fun selectSubCategory(category: Category) {
        _uiState.value = _uiState.value.copy(selectedSubCategory = category)
        Logger.d("`selectSubCategory` called{${category.name}}")
    }

    private fun selectParentCategory(category: Category) {
        _uiState.value = _uiState.value.copy(selectedParentCategory = category)
        _uiState.value =
            _uiState.value.copy(selectedSubCategory = Category.getSubCategories(category).first())

        Logger.d("`selectParentCategory` called{${category.name}}")
    }


    private fun insertTransaction() {

        val transaction: TransactionModel = TransactionModel(
            ledgerId = _uiState.value.selectedLedger?.id ?: 0,
            transactionDate = Clock.System.now(),
            category = _uiState.value.selectedSubCategory ?: Category.MISCELLANEOUS,
            transactionType = _uiState.value.selectedTransactionType,
            amount = _uiState.value.amount,
            currency = _uiState.value.selectedLedger?.currency ?: Currency.USD,
            remark = _uiState.value.remark,
            screenshotUri = "",
        )
        screenModelScope.launch {
            transactionRepository.insertTransaction(
                transaction
            )
        }
        Logger.d("`insertTransaction` called ￥{$transaction}")
    }

    private fun selectTab(tab: NewTransactionTab) {


        _uiState.value = _uiState.value.copy(selectedTab = tab)
        // initial numpad and category state
        _uiState.value = _uiState.value.copy(
            operand1 = "0.00",
            operator = "",
            operand2 = "",
            amountText = "0.00",
            selectedTransactionType = when (tab) {
                NewTransactionTab.INCOME -> TransactionType.INCOME
                NewTransactionTab.EXPENSE -> TransactionType.EXPENSE
            }
        )
        selectParentCategory(
            when (tab) {
                NewTransactionTab.INCOME -> Category.OTHER_INCOME
                NewTransactionTab.EXPENSE -> Category.MISCELLANEOUS
            }
        )
        Logger.d("`selectTab` called{${tab.name}}")
    }

    private fun setAmount(amount: BigDecimal) {
        _uiState.value = _uiState.value.copy(amount = amount)
        Logger.d("`setAmount` called{${amount.toPlainString()}}")
    }


    private fun selectCategory(category: Category) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
        Logger.d("`selectCategory` called{${category.name}}")
    }

    private fun setRemark(remark: String) {
        _uiState.value = _uiState.value.copy(remark = remark)
        Logger.d("`setRemark` called{${remark}}")
    }


    private fun toggleLedgerDropdown() {
        _uiState.value =
            _uiState.value.copy(isLedgerDropdownExpanded = !_uiState.value.isLedgerDropdownExpanded)
        Logger.d("`toggleLedgerDropdown` called{${_uiState.value.isLedgerDropdownExpanded}}")
    }

    private fun selectLedger(ledger: LedgerModel) {
        _uiState.value = _uiState.value.copy(selectedLedger = ledger)
        Logger.d("`selectLedger` called{${ledger.name}}")
    }


    private fun fetchLedgers() {
        screenModelScope.launch {
            val ledgers = ledgerRepository.getAllLedgers()
            _uiState.value = _uiState.value.copy(ledgers = ledgers)
        }
        Logger.d("`fetchLedgers` called")
    }


    // 以下是从 NumPadViewModel 移植过来的方法

    fun onNumPadButtonClicked(numPadButton: NumPadButton) {
        when (numPadButton) {
            is NumPadButton.Number -> handleNumber(numPadButton)
            is NumPadButton.Decimal -> handleDecimal()
            is NumPadButton.Backspace -> handleBackspace()
            is NumPadButton.Operator -> handleOperator(numPadButton)
            is NumPadButton.Function.AddAnotherTransaction -> handleAddAnotherTransaction()
            else -> Unit
        }
        updateDoneButtonState()
    }

    fun onDoneButtonClicked() {
        when (_uiState.value.doneButtonState) {
            //
            DoneButtonState.CANCEL -> {
                screenModelScope.launch {
                    _eventFlow.emit(NewTransactionUiEvent.NavigateBack)
                }
            }

            DoneButtonState.COMPLETE -> {
                val isValid = validateTransaction()
                if (isValid) {
                    insertTransaction()
                    _uiState.value = _uiState.value.copy(transactionCompleted = true)
                    screenModelScope.launch {
                        _eventFlow.emit(NewTransactionUiEvent.ShowSnackBar("Transaction completed"))
                        _eventFlow.emit(NewTransactionUiEvent.NavigateBack)
                    }

                }

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
        try {
            val amount = BigDecimal.parseString(_uiState.value.amountText)
            if (amount == BigDecimal.ZERO) {
                return
            }
        } catch (_: Exception) {
        }

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


    private fun validateTransaction(): Boolean {
        var errorMessage: String = ""
        var result = true
        try {
            val amount = BigDecimal.parseString(_uiState.value.amountText)
            if (amount <= BigDecimal.ZERO) {
                errorMessage = "Amount must be greater than 0"
                result = false
            }
            if (_uiState.value.selectedLedger == null) {
                errorMessage = "Please select a ledger"
                result = false
            }

            if (_uiState.value.selectedParentCategory == null) {
                errorMessage = "Please select a parent category"
                result = false
            }
            if (_uiState.value.selectedSubCategory == null) {
                errorMessage = "Please select a sub category"
                result = false
            }


        } catch (_: Exception) {
            Logger.d("Failed to parse amount: ${_uiState.value.amountText}")
        }
        if (!result) {
            screenModelScope.launch {
                _eventFlow.emit(NewTransactionUiEvent.ShowSnackBar(errorMessage))
            }

        }
        return result
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


            Logger.d(
                "processing ${operand1.toPlainString()} ${
                    when (operator) {
                        "+" -> "plus"
                        "-" -> "minus"
                        "×" -> "times"
                        "÷" -> "divided by"
                        else -> "unknown operator"
                    }
                } ${operand2.toPlainString()} = ${result.toPlainString()}"
            )

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



        _uiState.value = _uiState.value.copy(
            doneButtonState = newState
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
