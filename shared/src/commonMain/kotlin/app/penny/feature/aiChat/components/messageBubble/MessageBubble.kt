// MessageBubble.kt
package app.penny.feature.aiChat.components.messageBubble

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.ChatMessage
import app.penny.core.domain.model.SystemMessage
import app.penny.core.domain.model.UserModel
import app.penny.feature.aiChat.components.messageBubble.system.SystemAvatar
import app.penny.feature.aiChat.components.messageBubble.system.SystemMessageBubble
import app.penny.feature.aiChat.components.messageBubble.user.UserAvatar
import app.penny.feature.aiChat.components.messageBubble.user.UserMessageBubble

@Composable
fun MessageBubble(
    message: ChatMessage,
    onActionConfirm: (SystemMessage, Map<String, String?>) -> Unit,
    onActionDismiss: (SystemMessage) -> Unit
) {
    when (message.sender) {
        UserModel.System -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Start
            ) {

                SystemAvatar()
                Spacer(modifier = Modifier.width(8.dp))
                SystemMessageBubble(
                    message = message as SystemMessage,
                    onActionConfirm = onActionConfirm,
                    onActionDismiss = onActionDismiss
                )

            }
        }

        else -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.End
            ) {
                UserMessageBubble(message = message)
                Spacer(modifier = Modifier.width(8.dp))
                UserAvatar()
            }
        }
    }
}

