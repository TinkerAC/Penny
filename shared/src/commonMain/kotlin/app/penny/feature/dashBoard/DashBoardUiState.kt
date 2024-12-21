// file: shared/src/commonMain/kotlin/app/penny/feature/dashboard/DashboardUiState.kt
package app.penny.feature.dashBoard

import app.penny.core.domain.model.TransactionModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal

enum class RefreshIndicatorState(
    val message: String
) {
    None(""),
    ContinueToPull("继续下拉以刷新"),
    ReleaseToRefresh("释放以刷新"),
}

data class DashboardUiState(
    val scrollOffset: Float = 0f,                       // 整体内容的滚动偏移量
    val isRefreshing: Boolean = false,                  // 是否正在刷新
    val refreshIndicatorState: RefreshIndicatorState = RefreshIndicatorState.None,

    val incomeOfMonth: BigDecimal = BigDecimal.ZERO,      // 本月收入
    val expenseOfMonth: BigDecimal = BigDecimal.ZERO,     // 本月支出
    val balanceOfMonth: BigDecimal = BigDecimal.ZERO,     // 本月结余
    val recentTransactions: List<TransactionModel> = emptyList(),
)
