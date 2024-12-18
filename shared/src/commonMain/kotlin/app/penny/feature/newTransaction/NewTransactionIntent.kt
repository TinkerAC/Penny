package app.penny.feature.newTransaction

import app.penny.core.domain.enum.Category
import app.penny.core.domain.model.LedgerModel

sealed class NewTransactionIntent {
    data class SelectTab(val tab: NewTransactionTab) : NewTransactionIntent()
    data class SetRemark(val remark: String) : NewTransactionIntent()
    data class SelectLedger(val ledger: LedgerModel) : NewTransactionIntent()
    data object HideLedgerSelectDialog : NewTransactionIntent()
    data object ShowLedgerSelectDialog : NewTransactionIntent()
    data class SelectParentCategory(val category: Category) : NewTransactionIntent()
    data class SelectSubCategory(val category: Category) : NewTransactionIntent()
}