// file: shared/src/commonMain/kotlin/app/penny/feature/dashboard/DashboardScreen.kt
package app.penny.feature.dashBoard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.consumeAllChanges
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.penny.feature.newTransaction.NewTransactionScreen
import app.penny.presentation.ui.components.PennyLogo
import app.penny.presentation.ui.components.TransactionItem
import app.penny.provideNullAndroidOverscrollConfiguration
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.ionspin.kotlin.bignum.decimal.BigDecimal

class DashboardScreen : Screen {
    @OptIn(ExperimentalFoundationApi::class)
    @Composable
    override fun Content() {
        val viewModel = koinScreenModel<DashboardViewModel>()
        val uiState by viewModel.uiState.collectAsState()

        val coroutineScope = rememberCoroutineScope()

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
            modifier = Modifier.fillMaxSize().pointerInput(Unit) {
                detectVerticalDragGestures(onVerticalDrag = { change, dragAmount ->
                    if (dragAmount > 0 || listState.firstVisibleItemIndex == 0) { // 只在顶部或下拉时处理
                        change.consumeAllChanges()
                        viewModel.handleScroll(dragAmount)
                    }
                }, onDragEnd = {
                    viewModel.handleRelease()
                })
            }.background(
                MaterialTheme.colorScheme.surface
            )
        ) {
            CompositionLocalProvider(*provideNullAndroidOverscrollConfiguration()) {

                LazyColumn(

                    state = listState,
                    modifier = Modifier.fillMaxSize().background(Color.Transparent)

                ) {
                    item {
                        TopSection(
                            scale = scale, yOffset = yOffsetDp
                        ) // 传递动画后的缩放因子和偏移量
                    }

                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    item {
                        BottomSection(
                            uiState, modifier = Modifier.padding(
                                top = 120.dp, start = 16.dp, end = 16.dp
                            )
                        )
                    }

                    uiState.recentTransactions.forEach { transaction ->
                        item {
                            TransactionItem(transaction)
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(80.dp)) // 为下拉刷新留出空间
                    }
                }
            }
        }
    }


    @Composable
    fun TopSection(scale: Float, yOffset: Dp) {
        val topSurfaceHeight = 250.dp
        val cardHeight = 210.dp
        val rootNavigator = LocalNavigator.currentOrThrow

        Box(
            modifier = Modifier.fillMaxWidth().height(topSurfaceHeight),
            contentAlignment = Alignment.BottomCenter,

            ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.primaryContainer,
                shape = RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.BottomCenter
                ) {
                    PennyLogo(
                        fontSize = 56.sp, modifier = Modifier.scale(scale) // 应用缩放因子
                            .offset(y = (-150).dp - yOffset) // 应用垂直偏移量（向上移动）
                    )
                }
            }
            OverViewCard(modifier = Modifier.offset(0.dp, 130.dp),
                currencySymbol = "¥",
                incomeOfMonth = BigDecimal.fromInt(1000),
                expenseOfMonth = BigDecimal.fromInt(500),
                onAddTransactionClick = {
                    rootNavigator.push(NewTransactionScreen())
                }

            )
        }
    }

    @Composable
    fun RefreshIndicator(message: String, isRefreshing: Boolean) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (isRefreshing) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp), color = MaterialTheme.colorScheme.primary
                )
            } else {
                Text(
                    message,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }


    @Composable
    fun BottomSection(
        uiState: DashboardUiState, modifier: Modifier = Modifier
    ) {
        Column(
            modifier = modifier.fillMaxWidth()
        ) { // 当内容达到顶部并继续下滑时，显示提示文字
            AnimatedVisibility(
                visible = uiState.refreshIndicatorState != RefreshIndicatorState.None || uiState.isRefreshing,
                enter = fadeIn(animationSpec = TweenSpec(durationMillis = 300)),
                exit = fadeOut(animationSpec = TweenSpec(durationMillis = 300)),
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            ) {
                RefreshIndicator(
                    message = uiState.refreshIndicatorState.message,
                    isRefreshing = uiState.isRefreshing
                )
            }
            Text(
                text = "recent transactions",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
            )
        }
    }
}


@Composable
fun OverViewCard(
    currencySymbol: String,
    incomeOfMonth: BigDecimal,
    expenseOfMonth: BigDecimal,
    modifier: Modifier = Modifier,
    onAddTransactionClick: () -> Unit
) {

    var numberVisible by remember { mutableStateOf(true) }

    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .height(210.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                "本月支出",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Start)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (numberVisible) "$currencySymbol${expenseOfMonth.toPlainString()}" else "$currencySymbol****",
                    // 使用更大字号，比如 headlineMedium，使金额更加醒目
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )

                // 切换可见性的按钮
                IconButton(
                    onClick = { numberVisible = !numberVisible },
                    modifier = Modifier.align(Alignment.CenterVertically)
                ) {
                    Icon(
                        imageVector = if (numberVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff,
                        contentDescription = "Toggle number visibility"
                    )
                }
            }

            // 将本月收入与月结余放在同一行，并在行内再分为两个区块
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 本月收入
                Row {
                    Text(
                        "本月收入",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (numberVisible) "$currencySymbol${incomeOfMonth.toPlainString()}" else "$currencySymbol****",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                // 月结余
                Row {
                    Text(
                        "月结余",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (numberVisible) "$currencySymbol${(incomeOfMonth - expenseOfMonth).toPlainString()}" else "$currencySymbol****",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Column {
                Button(
                    onClick = { onAddTransactionClick() },
                    content = {
                        Text("记一笔")
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}
