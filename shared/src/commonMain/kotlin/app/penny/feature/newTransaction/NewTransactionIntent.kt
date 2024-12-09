package app.penny.feature.newTransaction

import app.penny.core.domain.enum.Category
import app.penny.core.domain.model.LedgerModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal

sealed class NewTransactionIntent {
    data class SelectTab(val tab: NewTransactionTab) : NewTransactionIntent()
    data class SetRemark(val remark: String) : NewTransactionIntent()
    data class SelectLedger(val ledger: LedgerModel) : NewTransactionIntent()
    data object ToggleLedgerDropdown : NewTransactionIntent()
    data class SelectParentCategory(val category: Category) : NewTransactionIntent()
    data class SelectSubCategory(val category: Category) : NewTransactionIntent()


}