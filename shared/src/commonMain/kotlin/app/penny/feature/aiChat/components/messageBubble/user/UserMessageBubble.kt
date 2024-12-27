// UserMessageBubble.kt
package app.penny.feature.aiChat.components.messageBubble.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
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
import app.penny.core.domain.model.ChatMessage
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.painterResource

@Composable
fun UserMessageBubble(message: ChatMessage) {
    Card(
        shape = MaterialTheme.shapes.medium.copy(
            bottomStart = CornerSize(16.dp),
            bottomEnd = CornerSize(16.dp),
            topStart = CornerSize(16.dp),
            topEnd = CornerSize(0.dp)
        ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primary),
        modifier = Modifier.widthIn(max = 240.dp)
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(
                text = message.content ?: "null",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimary
            )
        }
    }
}

@Composable
fun UserAvatar() {
    Image(
        painter = painterResource(SharedRes.images.avatar_boy),
        contentDescription = null,
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
    )
}
