// file: src/commonMain/kotlin/app/penny/feature/transactionDetail/TransactionDetailUiState.kt
package app.penny.feature.transactionDetail

import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.TransactionModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import dev.icerock.moko.resources.StringResource
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
data class TransactionDetailUiState(
    val transactionModel: TransactionModel = TransactionModel(),

    val alternativeTransactionType: List<TransactionType> = TransactionType.entries,
    val alternativeTransactionPrimaryCategory: List<Category> = emptyList(),
    val alternativeTransactionSecondaryCategory: List<Category> = emptyList(),

    // 只读账户
    val belongingLedger: LedgerModel = LedgerModel(),

    // 用户正在输入的字符串
    val amountText: String = "",

    // 最后一次成功解析并通过校验的金额
    val validatedAmount: BigDecimal = BigDecimal.ZERO,
    
    // 若有金额格式/数值错误，则存错误提示
    val amountInputError: StringResource? = null,
)