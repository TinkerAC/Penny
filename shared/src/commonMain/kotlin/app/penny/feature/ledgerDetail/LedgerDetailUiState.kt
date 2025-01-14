// file: src/commonMain/kotlin/app/penny/feature/ledgerDetail/LedgerDetailUiState.kt
package app.penny.feature.ledgerDetail

import app.penny.core.domain.model.LedgerModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal

data class LedgerDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val ledger: LedgerModel,
    val totalIncome: BigDecimal = BigDecimal.ZERO,
    val totalExpense: BigDecimal = BigDecimal.ZERO,
    val balance: BigDecimal = BigDecimal.ZERO,
    val ledgerName: String = ledger.name,
    val entryCount: Int = 0,
)