// UserMessageBubble.kt
package app.penny.feature.aiChat.components.messageBubble.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.penny.core.domain.model.ChatMessage
import app.penny.presentation.AvatarKamelConfig
import app.penny.presentation.ui.components.DefaultAvatar
import app.penny.shared.SharedRes
import dev.icerock.moko.resources.compose.painterResource
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.kamel.image.config.LocalKamelConfig


@Composable
fun UserMessageBubble(
    message: ChatMessage,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
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

}

@Composable
fun UserAvatar(
    imageUrl: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        CompositionLocalProvider(LocalKamelConfig provides AvatarKamelConfig) {
            KamelImage(
                resource = { asyncPainterResource(imageUrl) },
                contentDescription = "用户头像",
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape),
                contentScale = ContentScale.Crop,
                onLoading = {
                },
                onFailure = {
                    DefaultAvatar(
                        onClick = { }
                    )
                }
            )


        }
    }

}


@Composable
fun SystemAvatar(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(SharedRes.images.avatar_penny),
            contentDescription = null,
            modifier = Modifier
                .size(40.dp)
                .clip(CircleShape)
                .border(1.dp, MaterialTheme.colorScheme.onSurfaceVariant, CircleShape)
        )
    }

}