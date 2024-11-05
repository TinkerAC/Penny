package app.penny.presentation.ui.screens.newTransaction


import app.penny.domain.enum.ExpenseCategory
import app.penny.domain.enum.IncomeCategory
import app.penny.presentation.ui.components.numPad.DoneButtonState
import com.ionspin.kotlin.bignum.decimal.BigDecimal


data class NewTransactionUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val selectedIncomeCategory: IncomeCategory? = null,
    val selectedExpenseCategory: ExpenseCategory? = null,
    val remark: String = "",
    val amount: BigDecimal = BigDecimal.ZERO,
    val selectedTab: NewTransactionTab = NewTransactionTab.INCOME,
    // 添加 NumPad 的状态属性
    val operand1: String = "0.00",
    val operator: String = "",
    val operand2: String = "",
    val amountText: String = "0.00",
    val remarkText: String = "",
    val doneButtonState: DoneButtonState = DoneButtonState.CANCEL,
    val doneButtonText: String = "取消",
    val transactionCompleted: Boolean = false
)


enum class NewTransactionTab(
    val tabIndex: Int
) {
    INCOME(0),
    EXPENSE(1)
}