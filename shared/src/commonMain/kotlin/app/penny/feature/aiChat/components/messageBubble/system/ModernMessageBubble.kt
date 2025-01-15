import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.SystemMessage
import app.penny.feature.aiChat.components.messageBubble.system.IntentPendingContent
import app.penny.servershared.dto.BaseEntityDto
import app.penny.servershared.enumerate.SilentIntent
import app.penny.servershared.enumerate.UserIntentStatus
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SystemMessageBubble(
    modifier: Modifier = Modifier,
    message: SystemMessage,
    onActionConfirm: (SystemMessage, BaseEntityDto?) -> Unit,
    onActionDismiss: (SystemMessage) -> Unit
) {
    Column(modifier = modifier) {
        Surface(
            shape = MaterialTheme.shapes.medium.copy(
                topStart = CornerSize(0.dp),
                topEnd = CornerSize(16.dp),
                bottomEnd = CornerSize(16.dp),
                bottomStart = CornerSize(16.dp)
            ),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 2.dp,
            modifier = Modifier
                .widthIn(min = 40.dp, max = 280.dp)
                .padding(horizontal = 4.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // 1) 标题 & 状态(Chip) 行
                if (message.userIntent !is SilentIntent) {
                    TitleAndStatusRow(message)
                }

                // 2) 主要内容
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
                            else -> {}
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
}

@Composable
private fun TitleAndStatusRow(
    message: SystemMessage
) {
    val userIntent = message.userIntent
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(userIntent.displayText),
            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(7f)
        )

        Column(
            horizontalAlignment = Alignment.End,
            modifier = Modifier.weight(3f)
        ) {
            StatusChip(status = userIntent.status)
        }
    }
}

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

    AssistChip(
        onClick = { /* no-op */ },
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        colors = AssistChipDefaults.assistChipColors(
            containerColor = containerColor,
            labelColor = contentColor
        ),
        modifier = Modifier
    )
}
