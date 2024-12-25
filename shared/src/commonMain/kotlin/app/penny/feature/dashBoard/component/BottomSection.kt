package app.penny.feature.dashBoard.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.penny.feature.dashBoard.DashboardUiState
import app.penny.feature.dashBoard.RefreshIndicatorState
import app.penny.feature.transactionDetail.TransactionDetailScreen
import app.penny.presentation.ui.components.TransactionItem
import app.penny.shared.SharedRes
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource
import kotlin.uuid.ExperimentalUuidApi

@OptIn(ExperimentalUuidApi::class)
@Composable
fun BottomSection(
    uiState: DashboardUiState,
    modifier: Modifier = Modifier
) {

    val rootNavigator = LocalNavigator.currentOrThrow
    Column(
        modifier = modifier.fillMaxWidth()
    ) {
        // 刷新指示器
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

        // 标题
        Text(
            text =
            stringResource(SharedRes.strings.recent_transactions),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        )

        // 当没有最近的交易记录时显示提示文本和图片
        if (uiState.recentTransactions.isEmpty()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(SharedRes.images.no_transaction_found),
                    contentDescription = "No transaction found",
                    modifier = Modifier
                        .size(150.dp)
                        .padding(bottom = 16.dp)
                )
                Text(
                    text = stringResource(SharedRes.strings.no_transaction_notice),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center
                )
            }
        } else {
            // 显示交易列表
            uiState.recentTransactions.forEach { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = {
                        rootNavigator.push(
                            TransactionDetailScreen(
                                transactionUuid = transaction.uuid
                            )
                        )
                    }
                )

            }
        }
    }
}


@Composable
private fun RefreshIndicator(message: String, isRefreshing: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
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
