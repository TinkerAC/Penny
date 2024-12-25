// file: src/commonMain/kotlin/app/penny/feature/transactionDetail/TransactionDetailIntent.kt
package app.penny.feature.transactionDetail

import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.TransactionType

sealed class TransactionDetailIntent {
    data class SelectPrimaryCategory(val category: Category) : TransactionDetailIntent()

    data class SelectSecondaryCategory(val category: Category) : TransactionDetailIntent()

    data class SelectTransactionType(val transactionType: TransactionType) :
        TransactionDetailIntent()

    data object SaveTransaction : TransactionDetailIntent()
    data object DeleteTransaction : TransactionDetailIntent()

    // 更新备注（即时更新）
    data class UpdateRemark(val remark: String) : TransactionDetailIntent()

    // 金额输入框每次文本变更
    data class UpdateAmountText(val text: String) : TransactionDetailIntent()

    // 在失焦或点击完成等时机，校验金额（>0 且可被正确解析）
    data object ValidateAmount : TransactionDetailIntent()

    // 点击时间字段 -> 只读提示
    data object ClickTimeField : TransactionDetailIntent()

    // 点击账户字段 -> 只读提示
    data object ClickLedgerField : TransactionDetailIntent()
}