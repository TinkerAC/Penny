package app.penny.presentation.ui.screens.newTransaction

import app.penny.domain.enum.ExpenseCategory
import app.penny.domain.enum.IncomeCategory
import app.penny.domain.model.TransactionModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal

sealed class NewTransactionIntent {
    data class SelectIncomeCategory(val category: IncomeCategory) : NewTransactionIntent()
    data class SelectExpenseCategory(val category: ExpenseCategory) : NewTransactionIntent()
    data class SetAmount(val amount: BigDecimal) : NewTransactionIntent()
    data class SelectTab(val tab: NewTransactionTab) : NewTransactionIntent()
    data class SetRemark(val remark: String) : NewTransactionIntent()
    data class InsertTransaction(val transaction: TransactionModel) : NewTransactionIntent()
}