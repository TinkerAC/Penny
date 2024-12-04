// file: shared/src/commonMain/kotlin/app/penny/feature/aiChat/components/ChatBubble.kt
package app.penny.feature.aiChat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.ChatMessage
import org.jetbrains.compose.resources.painterResource
import penny.shared.generated.resources.Res
import penny.shared.generated.resources.avatar_boy
import penny.shared.generated.resources.avatar_girl
import penny.shared.generated.resources.avatar_penny

import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import androidx.compose.foundation.shape.CircleShape

@OptIn(ExperimentalUuidApi::class)

@Composable
fun ChatBubble(message: ChatMessage) {
    val isUser = message.sender.uuid != Uuid.fromLongs(0, 0)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            // AI Avatar
            Image(
                painter = painterResource(
                    Res.drawable.avatar_penny
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp) // 控制头像大小
                    .clip(CircleShape) // 将图像裁剪为圆形
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape) // 设置圆形边框
            )
            Spacer(modifier = Modifier.width(8.dp))
        }
        Card(
            shape = MaterialTheme.shapes.medium.copy(
                topStart = CornerSize(16.dp),
                topEnd = CornerSize(16.dp),
                bottomStart = if (isUser) CornerSize(16.dp) else CornerSize(0.dp),
                bottomEnd = if (isUser) CornerSize(0.dp) else CornerSize(16.dp)
            ),
            colors = if (isUser) {
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary)
            } else {
                CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            },
            modifier = Modifier.widthIn(max = 240.dp)
        ) {
            when (message) {
                is ChatMessage.TextMessage -> {
                    Text(
                        text = message.content,
                        style = MaterialTheme.typography.bodyMedium,
                        color = if (isUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(8.dp)
                    )
                }

//                is ChatMessage.AudioMessage -> {
//                    // Display audio message with duration
//                    Text(
//                        text = "Audio Message (${message.duration} sec)",
//                        style = MaterialTheme.typography.bodyMedium,
//                        modifier = Modifier.padding(8.dp)
//                    )
//                }
            }
        }
        if (isUser) {
            Spacer(modifier = Modifier.width(8.dp))

            // User Avatar
            Image(
                painter = painterResource(
                    if (Random.nextBoolean()) Res.drawable.avatar_girl else Res.drawable.avatar_boy // TODO: 传入用户头像uri，用于显示用户头像
                ),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp) // 控制头像大小
                    .clip(CircleShape) // 将图像裁剪为圆形
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape) // 设置圆形边框
            )
        }
    }
}