// file: composeApp/src/commonMain/kotlin/app/penny/presentation/ui/screens/newTransaction/NewTransactionViewModel.kt
package app.penny.feature.newTransaction


import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserPreferenceRepository
import app.penny.core.domain.enumerate.Category
import app.penny.core.domain.enumerate.Currency
import app.penny.core.domain.enumerate.TransactionType
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.usecase.SyncDataUseCase
import app.penny.feature.newTransaction.component.DoneButtonState
import app.penny.feature.newTransaction.component.NumPadButton
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
class NewTransactionViewModel(
    private val transactionRepository: TransactionRepository,
    private val ledgerRepository: LedgerRepository,
    private val userDataRepository: UserDataRepository,
    private val userPreferenceRepository: UserPreferenceRepository,
    private val syncDataUseCase: SyncDataUseCase
) : ScreenModel {

    private val _uiState = MutableStateFlow(
        NewTransactionUiState()
    )
    val uiState: StateFlow<NewTransactionUiState> = _uiState.asStateFlow()


    private val _eventFlow = MutableSharedFlow<NewTransactionUiEvent>(
        replay = 0
    )
    val eventFlow = _eventFlow.asSharedFlow()


    private val decimalMode =
        DecimalMode(20, roundingMode = RoundingMode.ROUND_HALF_CEILING, scale = 2)


    fun refreshData() {
        screenModelScope.launch {
            fetchLedgers()
            _uiState.value = _uiState.value.copy(
                selectedLedger = userDataRepository.getDefaultLedger()
            )
            selectParentCategory(
                when (_uiState.value.selectedTab) {
                    NewTransactionTab.INCOME -> Category.getSubCategories(Category.INCOME).first()
                    NewTransactionTab.EXPENSE -> Category.getSubCategories(Category.EXPENSE).first()
                }
            )
        }
    }

    private var autoCloudSync: Boolean = false


    init {
        refreshData()
        autoCloudSync = userPreferenceRepository.getAutoCloudSyncEnabled()

    }

    fun handleIntent(intent: NewTransactionIntent) {
        when (intent) {
            is NewTransactionIntent.SelectTab -> selectTab(intent.tab)
            is NewTransactionIntent.SetRemark -> setRemark(intent.remark)
            is NewTransactionIntent.HideLedgerSelectDialog -> toggleLedgerSelectDialog()
            is NewTransactionIntent.SelectLedger -> selectLedger(intent.ledger)
            is NewTransactionIntent.SelectParentCategory -> selectParentCategory(intent.category)
            is NewTransactionIntent.SelectSubCategory -> selectSubCategory(intent.category)
            NewTransactionIntent.ShowLedgerSelectDialog -> toggleLedgerSelectDialog()
            NewTransactionIntent.HideNumPad -> {
                _uiState.value = _uiState.value.copy(showNumPad = false)
            }

            NewTransactionIntent.ShowNumPad -> {
                _uiState.value = _uiState.value.copy(showNumPad = true)
            }
        }
    }

    private fun selectSubCategory(category: Category) {
        _uiState.value = _uiState.value.copy(selectedSubCategory = category)
        Logger.d("`selectSubCategory` called{${category.name}}")
    }

    private fun selectParentCategory(category: Category) {
        _uiState.value = _uiState.value.copy(selectedParentCategory = category)
        Logger.d("`selectParentCategory` called{${category.name}}")
        Logger.d(
            "`selectParentCategory` called{${
                Category.getSubCategories(category).first().name
            }}"
        )
        _uiState.value =
            _uiState.value.copy(selectedSubCategory = Category.getSubCategories(category).first())
    }

    private fun insertTransaction() {
        val transaction = TransactionModel(
            ledgerUuid = _uiState.value.selectedLedger!!.uuid,
            transactionInstant = Clock.System.now(),
            category = _uiState.value.selectedSubCategory ?: Category.getLevel1Categories().first(),
            transactionType = _uiState.value.selectedTransactionType,
            amount = BigDecimal.parseString(_uiState.value.amountText),
            currency = _uiState.value.selectedLedger?.currency ?: Currency.USD,
            screenshotUri = "",
            remark = _uiState.value.remark,
        )
        screenModelScope.launch {
            transactionRepository.insert(
                transaction
            )
        }
        Logger.d("`insertTransaction` called ￥{$transaction}")
    }

    private fun selectTab(tab: NewTransactionTab) {
        _uiState.value = _uiState.value.copy(selectedTab = tab)
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
                NewTransactionTab.INCOME -> Category.getIncomeCategories().first()
                NewTransactionTab.EXPENSE -> Category.getExpenseCategories().first()
            }
        )
        Logger.d("`selectTab` called{${tab.name}}")
    }

    private fun setRemark(remark: String) {
        _uiState.value = _uiState.value.copy(remark = remark)
        Logger.d("`setRemark` called{${remark}}")
    }

    private fun toggleLedgerSelectDialog() {
        _uiState.value =
            _uiState.value.copy(ledgerSelectDialogVisible = !_uiState.value.ledgerSelectDialogVisible)
    }

    private fun selectLedger(ledger: LedgerModel) {
        _uiState.value = _uiState.value.copy(selectedLedger = ledger)
        Logger.d("`selectLedger` called{${ledger.name}}")
    }

    private suspend fun fetchLedgers() {
        val user = userDataRepository.getUser()
        val ledgers = ledgerRepository.findByUserUuid(user.uuid)
        _uiState.value = _uiState.value.copy(ledgers = ledgers)

        Logger.d("`fetchLedgers` called")
    }

    // NumPad 相关逻辑
    fun onNumPadButtonClicked(numPadButton: NumPadButton) {
        when (numPadButton) {
            is NumPadButton.Number -> handleNumber(numPadButton)
            is NumPadButton.Decimal -> handleDecimal()
            is NumPadButton.Backspace -> handleBackspace()
            is NumPadButton.Operator -> handleOperator(numPadButton)
            is NumPadButton.Function.AddAnotherTransaction -> {}
            else -> Unit
        }
        updateDoneButtonState()
    }

    fun onDoneButtonClicked() {
        when (_uiState.value.doneButtonState) {
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
                        // Show snackbar
                        _eventFlow.emit(
                            NewTransactionUiEvent.ShowSnackBar(
                                "Transaction completed",
                                pop = true
                            )
                        )
                        // hide num pad
                        _uiState.value = _uiState.value.copy(showNumPad = false)

                        try {
                            if (autoCloudSync) {
                                syncDataUseCase()
                            }
                        } catch (e: Exception) {
                            Logger.d("Failed to sync transaction: ${e.message}")
                        }

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
            _uiState.value = _uiState.value.copy(operand1 = "-")
        } else if (_uiState.value.operator.isEmpty()) {
            _uiState.value = _uiState.value.copy(operator = operatorText)
        } else if (_uiState.value.operand2.isEmpty()) {
            _uiState.value = _uiState.value.copy(operator = operatorText)
        } else {
            calculate()
            _uiState.value = _uiState.value.copy(operator = operatorText, operand2 = "")
        }
        updateAmountText()
    }

    private fun handleAddAnotherTransaction() {
        try {
            calculate()
            onDoneButtonClicked()
            _uiState.update {
                it.copy(
                    operand1 = "0.00",
                    operator = "",
                    operand2 = "",
                    amountText = "0.00",
                    remarkText = "",
                    doneButtonState = DoneButtonState.CANCEL
                )
            }
        } catch (e: Exception) {
            Logger.d("Failed to add ,${e.message}")
        }
    }

    private fun validateTransaction(): Boolean {
        var errorMessage = ""
        var result = true
        try {
            val amount = BigDecimal.parseString(_uiState.value.amountText)
            if (amount <= BigDecimal.ZERO) {
                errorMessage = "Amount must be greater than 0"
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
                _eventFlow.emit(NewTransactionUiEvent.ShowSnackBar(errorMessage, pop = false))
            }
        }
        return result
    }

    private fun calculate() {
        val operand1Str = _uiState.value.operand1
        val operator = _uiState.value.operator
        val operand2Str = _uiState.value.operand2

        if (operator.isEmpty() || operand2Str.isEmpty()) {
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
        val hasError = operand1 == "Error"

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