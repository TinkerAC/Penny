// file: shared/src/commonMain/kotlin/app/penny/feature/dashboard/DashboardViewModel.kt
package app.penny.feature.dashBoard

import cafe.adriel.voyager.core.model.ScreenModel
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel : ScreenModel {
    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState

    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())

    // 下拉刷新触发阈值（可根据实际需求调整）
    private val refreshThreshold = 80f

    fun handleScroll(delta: Float) {
        _uiState.update { state ->
            val newOffset = state.scrollOffset + delta
            // 限制最大下拉/上推幅度以免无限拉动
            val constrainedOffset = newOffset.coerceIn(-250f, 100f)

            val refreshState = when {
                constrainedOffset > refreshThreshold -> RefreshIndicatorState.ReleaseToRefresh
                constrainedOffset > 0f -> RefreshIndicatorState.ContinueToPull
                else -> RefreshIndicatorState.None
            }

            Logger.d { "refreshState: $refreshState, constrainedOffset: $constrainedOffset" }

            state.copy(
                scrollOffset = constrainedOffset,
                refreshIndicatorState = refreshState
            )
        }
    }

    fun handleRelease() {
        coroutineScope.launch {
            val currentState = _uiState.value
            if (currentState.refreshIndicatorState == RefreshIndicatorState.ReleaseToRefresh) {
                // 达到释放刷新条件，触发刷新
                _uiState.update { it.copy(isRefreshing = true) }
                Logger.d { "执行数据同步……" }

                delay(2000) // 模拟网络请求

                // 刷新完成回到初始位置
                animateScrollOffset(currentState.scrollOffset, 0f)
                _uiState.update {
                    it.copy(
                        isRefreshing = false,
                        refreshIndicatorState = RefreshIndicatorState.None
                    )
                }
            } else {
                // 未达到刷新条件则回弹至0或保持当前位置(如果向上滚动过多，也可回到0)
                if (currentState.scrollOffset < -200)
                    animateScrollOffset(currentState.scrollOffset, -250f)
                else
                    animateScrollOffset(currentState.scrollOffset, 0f)
            }

            if (currentState.scrollOffset != 0f) {
                _uiState.update { it.copy(refreshIndicatorState = RefreshIndicatorState.None) }
            }
        }
    }

    private suspend fun animateScrollOffset(from: Float, to: Float) {
        // 简单线性动画
        val steps = 15
        val stepValue = (to - from) / steps
        for (i in 1..steps) {
            delay(10)
            _uiState.update {
                it.copy(scrollOffset = it.scrollOffset + stepValue)
            }
        }
        _uiState.update { it.copy(scrollOffset = to) }
    }
}
