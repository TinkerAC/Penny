// SystemMessageBubble.kt
package app.penny.feature.aiChat.components.messageBubble.system

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.SystemMessage
import app.penny.servershared.enumerate.SilentIntent
import app.penny.servershared.enumerate.UserIntentStatus
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.painterResource
import dev.icerock.moko.resources.compose.stringResource

@Composable
fun SystemMessageBubble(
    message: SystemMessage,
    onActionConfirm: (SystemMessage, Map<String, String?>) -> Unit,
    onActionDismiss: (SystemMessage) -> Unit
) {
    Card(
        shape = MaterialTheme.shapes.medium.copy(
            bottomStart = CornerSize(16.dp),
            bottomEnd = CornerSize(16.dp),
            topStart = CornerSize(0.dp),
            topEnd = CornerSize(16.dp)
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.widthIn(max = 240.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = message.content ?: "",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            when (message.userIntent) {
                is SilentIntent -> {
                    SilentIntentMessageContent(message = message)
                }

                else -> {
                    when (message.userIntent.status) {
                        UserIntentStatus.Pending -> {
                            IntentPendingContent(
                                message = message,
                                onConfirm = { editedFields ->
                                    onActionConfirm(message, editedFields)
                                },
                                onDismiss = {
                                    onActionDismiss(message)
                                }
                            )
                        }

                        UserIntentStatus.Completed -> {
                            ActionCompletedContent(userIntent = message.userIntent)
                        }

                        UserIntentStatus.Cancelled -> {
                            ActionCancelledContent(userIntent = message.userIntent)
                        }

                        UserIntentStatus.Failed -> {
                            Text(
                                text = stringResource(SharedRes.strings.action_failed),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            message.executeLog?.let {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = it,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun SystemAvatar() {
    Image(
        painter = painterResource(SharedRes.images.avatar_penny),
        contentDescription = null,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
    )
}



