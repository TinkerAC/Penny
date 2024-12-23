package app.penny.feature.transactionDetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Surface
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.TransactionModel
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.koinScreenModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class TransactionDetailScreen @OptIn(ExperimentalUuidApi::class) constructor(
    private val transactionUuid: Uuid,
) : Screen {
    @OptIn(ExperimentalUuidApi::class)
    @Composable
    override fun Content() {
        val viewModel: TransactionDetailViewModel = koinScreenModel<TransactionDetailViewModel>(
            parameters = { parametersOf(transactionUuid) }
        )

        val uiState = viewModel.uiState.collectAsState()



        EditExpenseScreen(
            transactionModel = uiState.value.transactionModel,
        )

    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseScreen(
    transactionModel: TransactionModel,
    onBackClick: () -> Unit = {},
    onConfirmClick: () -> Unit = {},
    onDeleteClick: () -> Unit = {},
    onChangeToTransferClick: () -> Unit = {},
    onFinishClick: () -> Unit = {},
) {

    Scaffold(topBar = {
        TopAppBar(
            title = { Text(text = "编辑支出") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack, contentDescription = "Back"
                    )
                }
            },
            actions = {
                IconButton(onClick = onConfirmClick) {
                    Icon(
                        imageVector = Icons.Default.Check, contentDescription = "Confirm"
                    )
                }
            },
            // 可根据需求自定义背景色等
            // colors = TopAppBarDefaults.mediumTopAppBarColors(
            //     containerColor = MaterialTheme.colorScheme.surface
            // )
        )
    }, bottomBar = {
        // 底部操作区域
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // “删除”按钮
            OutlinedButton(
                onClick = onDeleteClick, modifier = Modifier.weight(1f)
            ) {
                Text("删除")
            }
            // “改为转账”按钮
            OutlinedButton(
                onClick = onChangeToTransferClick, modifier = Modifier.weight(1f)
            ) {
                Text("改为转账")
            }
            // “完成”按钮
            Button(
                onClick = onFinishClick, modifier = Modifier.weight(1f)
            ) {
                Text("完成")
            }
        }
    }) { innerPadding ->
        // 主体内容区域
        Column(
            modifier = Modifier.padding(innerPadding).padding(horizontal = 16.dp, vertical = 8.dp),
        ) {
            // 金额
            Text(
                text = "438.00", style = MaterialTheme.typography.displayMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )

            // 分割线
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
            )
            Spacer(modifier = Modifier.height(8.dp))

            // 类别
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Category, // 仅作示例，可自定义图标
                    contentDescription = "category", tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "食品酒水 > 早午晚餐", style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 账户
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AccountBalanceWallet,
                    contentDescription = "account",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "现金 (CNY)", style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 时间
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = "time",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "12月14日 16:12", style = MaterialTheme.typography.bodyMedium
                )
                Spacer(Modifier.width(8.dp))
                // 右侧的“X”可以是一个按钮，用于清除/重置时间
                IconButton(onClick = { /* 重置时间 */ }) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "clear time",
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 备注
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "memo",
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(Modifier.width(4.dp))
                Text(
                    text = "杀随手记开发者的马", style = MaterialTheme.typography.bodyMedium
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // 标签（如：成员、商家、项目）
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                // 演示使用小Chip
                ChipItem("成员")
                ChipItem("商家")
                ChipItem("项目")
            }
        }
    }
}

/**
 * 简单演示一下如何使用 Box 或 Surface 来做一个“Chip”样式
 */
@Composable
fun ChipItem(text: String) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceVariant, shape = MaterialTheme.shapes.small
    ) {
        Text(
            text = text, style = MaterialTheme.typography.labelMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant
            ), modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}