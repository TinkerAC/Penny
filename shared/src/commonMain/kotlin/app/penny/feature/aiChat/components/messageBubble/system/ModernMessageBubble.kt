import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.SystemMessage

import app.penny.feature.aiChat.components.messageBubble.system.IntentPendingContent
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.SilentIntent
import app.penny.servershared.enumerate.UserIntentStatus
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun ModernSystemMessageBubble(
    message: SystemMessage,
    onActionConfirm: (SystemMessage, BaseEntityDto?) -> Unit,
    onActionDismiss: (SystemMessage) -> Unit
) {
    // 外层只包裹气泡自身，不填充父容器的宽度
    Surface(
        shape = MaterialTheme.shapes.medium.copy(
            topStart = CornerSize(0.dp),    // 左上角贴合行方向
            topEnd = CornerSize(16.dp),
            bottomEnd = CornerSize(16.dp),
            bottomStart = CornerSize(16.dp)
        ),
        color = MaterialTheme.colorScheme.surfaceVariant,
//        tonalElevation = 1.dp,
        modifier = Modifier
            .widthIn(min = 40.dp, max = 280.dp) // 控制泡泡最大宽度
            .padding(horizontal = 4.dp)         // 与头像或边缘留点间隙
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // 1) 标题 & 状态(Chip) 行 （可选：若需要在气泡顶部显示 “系统消息”+状态等）
            if (message.userIntent !is SilentIntent) {
                TitleAndStatusRow(message)

            }

            // 2) 主要内容：展示 message.content
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = message.content ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            // 3) 根据 userIntent 不同，展示不同的 UI
            Spacer(modifier = Modifier.height(8.dp))
            when (message.userIntent) {
                else -> {
                    when (message.userIntent.status) {
                        UserIntentStatus.Pending -> {
                            IntentPendingContent(
                                message = message,
                                onConfirm = { baseEntityDto ->
                                    onActionConfirm(message, baseEntityDto)
                                },
                                onDismiss = {
                                    onActionDismiss(message)
                                }
                            )
                        }


                        else -> {

                        }
                    }
                }
            }

            // 4) 执行日志（若有）
            message.executeLog?.let { logText ->
                if (logText.isNotBlank()) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = logText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * 标题 + 状态区：若你不需要此行，可以去掉或将 statusChip 的逻辑移动到下方。
 */
@Composable
private fun TitleAndStatusRow(
    message: SystemMessage
) {
    // 如果不想展示标题，可视情况删除此函数
    val userIntent = message.userIntent
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 左侧展示 Intent 的名称 // bold
        Text(
            text = stringResource(userIntent.displayText), // 如：插入账本、插入交易等
            style = MaterialTheme.typography.labelLarge.copy(
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(7f)
        )

        // 右侧展示状态Chip
        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.weight(3f)
        ) {
            StatusChip(status = userIntent.status)
        }
    }
}

/**
 * 状态Chip，仅展示状态文本，如 "Pending"、"Completed" 等。
 * 可根据状态自定义颜色和图标。
 */
@Composable
private fun StatusChip(status: UserIntentStatus) {
    val label = when (status) {
        UserIntentStatus.Pending -> stringResource(SharedRes.strings.status_pending)
        UserIntentStatus.Completed -> stringResource(SharedRes.strings.status_completed)
        UserIntentStatus.Cancelled -> stringResource(SharedRes.strings.status_cancelled)
        UserIntentStatus.Failed -> stringResource(SharedRes.strings.status_failed)
    }

    val containerColor = when (status) {
        UserIntentStatus.Pending -> MaterialTheme.colorScheme.secondaryContainer
        UserIntentStatus.Completed -> MaterialTheme.colorScheme.tertiaryContainer
        UserIntentStatus.Cancelled -> MaterialTheme.colorScheme.surfaceVariant
        UserIntentStatus.Failed -> MaterialTheme.colorScheme.errorContainer
    }
    val contentColor = when (status) {
        UserIntentStatus.Pending -> MaterialTheme.colorScheme.onSecondaryContainer
        UserIntentStatus.Completed -> MaterialTheme.colorScheme.onTertiaryContainer
        UserIntentStatus.Cancelled -> MaterialTheme.colorScheme.onSurfaceVariant
        UserIntentStatus.Failed -> MaterialTheme.colorScheme.onErrorContainer
    }

    androidx.compose.material3.AssistChip(
        onClick = { /* no-op */ },
        label = { Text(text = label, style = MaterialTheme.typography.labelMedium) },
        colors = androidx.compose.material3.AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = contentColor
        ),
        modifier = Modifier
    )
}