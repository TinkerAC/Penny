package app.penny.feature.newTransaction


import androidx.compose.material3.SnackbarHostState
import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.LedgerModel
import app.penny.presentation.ui.components.numPad.DoneButtonState
import com.ionspin.kotlin.bignum.decimal.BigDecimal


data class NewTransactionUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val remark: String = "",
    val amount: BigDecimal = BigDecimal.ZERO,
    val selectedTab: NewTransactionTab = NewTransactionTab.EXPENSE,

    val snackbarHostState: SnackbarHostState = SnackbarHostState(),

    val selectedTransactionType: TransactionType = TransactionType.EXPENSE,

    val ledgers: List<LedgerModel> = emptyList(),
    val selectedLedger: LedgerModel? = null,
    val isLedgerDropdownExpanded: Boolean = false,

    val selectedParentCategory: Category? = null,
    val selectedSubCategory: Category? = null,

    // 添加 NumPad 的状态属性
    val operand1: String = "0.00",
    val operator: String = "",
    val operand2: String = "",
    val amountText: String = "0.00",
    val remarkText: String = "",
    val doneButtonState: DoneButtonState = DoneButtonState.CANCEL,
    val transactionCompleted: Boolean = false
)


enum class NewTransactionTab(
    val tabIndex: Int
) {
    EXPENSE(0),

    INCOME(1),

}