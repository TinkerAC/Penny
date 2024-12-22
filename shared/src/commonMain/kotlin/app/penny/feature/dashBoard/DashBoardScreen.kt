// file: shared/src/commonMain/kotlin/app/penny/feature/dashboard/DashboardScreen.kt
package app.penny.feature.dashBoard

import app.penny.feature.dashBoard.component.TopSection
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import app.penny.feature.dashBoard.component.BottomSection
import app.penny.provideNullAndroidOverscrollConfiguration
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel

class DashboardScreen : Screen {
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<DashboardViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(key1 = viewModel) {
            viewModel.refreshData()
        }

        val listState = rememberLazyListState()

        // 定义最大偏移量和缩放范围
        val maxOffsetPx = 200f // 最大滚动偏移量（像素）
        val minScale = 0.5f
        val maxScale = 1.0f

        // 获取当前的密度，用于将像素转换为 Dp
        val density = LocalDensity.current

        // 计算缩放因子
        val scale by remember {
            derivedStateOf {
                if (listState.firstVisibleItemIndex == 0) {
                    val offset = listState.firstVisibleItemScrollOffset.toFloat()
                    // 线性计算缩放因子，并限制在 [minScale, maxScale] 之间
                    val scaleFactor =
                        maxScale - ((maxScale - minScale) * (offset / maxOffsetPx).coerceIn(0f, 1f))
                    scaleFactor
                } else {
                    minScale
                }
            }
        }

        // 计算垂直偏移量（Dp）
        val yOffsetDp by remember {
            derivedStateOf {
                if (listState.firstVisibleItemIndex == 0) {
                    val offset = listState.firstVisibleItemScrollOffset.toFloat()
                    // 线性计算偏移量，并限制在 [0.dp, maxOffsetDp] 之间
                    val yOffsetPx = (offset / maxOffsetPx).coerceIn(0f, 1f) * 100f // 最大偏移量为100.dp
                    with(density) { yOffsetPx.toDp() }
                } else {
                    with(density) { 100f.toDp() } // 最大偏移量
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectVerticalDragGestures(
                        onVerticalDrag = { change, dragAmount ->
                            if (dragAmount > 0 || listState.firstVisibleItemIndex == 0) { // 只在顶部或下拉时处理
                                change.consume()
                                viewModel.handleScroll(dragAmount)
                            }
                        },
                        onDragEnd = {
                            viewModel.handleRelease()
                        }
                    )
                }
                .background(
                    MaterialTheme.colorScheme.surface
                )
        ) {
            CompositionLocalProvider(*provideNullAndroidOverscrollConfiguration()) {

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Transparent)
                ) {
                    item {
                        TopSection(
                            scale = scale,
                            yOffset = yOffsetDp,
                            uiState = uiState
                        ) // 传递动画后的缩放因子和偏移量
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        BottomSection(
                            uiState = uiState, modifier = Modifier.padding(
                                top = 120.dp, start = 16.dp, end = 16.dp
                            )
                        )
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // 为下拉刷新留出空间
                    }
                }
            }
        }
    }


}
