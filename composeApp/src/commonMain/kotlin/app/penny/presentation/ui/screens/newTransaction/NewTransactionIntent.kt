package app.penny.presentation.ui.screens.newTransaction

import app.penny.domain.enum.Category
import app.penny.domain.model.LedgerModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal

sealed class NewTransactionIntent {
    data class SelectCategory(val category: Category) : NewTransactionIntent()
    data class SetAmount(val amount: BigDecimal) : NewTransactionIntent()
    data class SelectTab(val tab: NewTransactionTab) : NewTransactionIntent()
    data class SetRemark(val remark: String) : NewTransactionIntent()
    data class SelectLedger(val ledger: LedgerModel) : NewTransactionIntent()
    data object ToggleLedgerDropdown : NewTransactionIntent()

    data class SelectParentCategory(val category: Category) : NewTransactionIntent()
    data class SelectSubCategory(val category: Category) : NewTransactionIntent()


}