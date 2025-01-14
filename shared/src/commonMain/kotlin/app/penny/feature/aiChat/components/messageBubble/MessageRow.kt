// MessageBubble.kt
package app.penny.feature.aiChat.components.messageBubble

import SystemMessageBubble
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.ChatMessage
import app.penny.core.domain.model.SystemMessage
import app.penny.core.domain.model.UserModel
import app.penny.feature.aiChat.components.messageBubble.user.SystemAvatar
import app.penny.feature.aiChat.components.messageBubble.user.UserAvatar
import app.penny.feature.aiChat.components.messageBubble.user.UserMessageBubble
import app.penny.servershared.dto.BaseEntityDto

@Composable
fun MessageRow(
    userAvatarUrl: String,
    message: ChatMessage,
    onActionConfirm: (SystemMessage, BaseEntityDto?) -> Unit,
    onActionDismiss: (SystemMessage) -> Unit
) {
    when (message.sender) {
        UserModel.System -> {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.Top // 改为顶部对齐
            ) {
                SystemAvatar(
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
                Spacer(modifier = Modifier.width(8.dp))
                SystemMessageBubble(
                    modifier = Modifier.offset(y = 8.dp), // 根据需要调整偏移量
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
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.Top // 改为顶部对齐
            ) {
                UserMessageBubble(
                    modifier = Modifier.offset(y = 8.dp), // 根据需要调整偏移量
                    message = message
                )
                Spacer(modifier = Modifier.width(8.dp))
                UserAvatar(
                    imageUrl = userAvatarUrl,
                    modifier = Modifier.align(Alignment.CenterVertically)
                )
            }
        }
    }
}


