package app.penny.presentation.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import app.penny.presentation.AvatarKamelConfig
import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpUrlFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.KamelImage
import io.kamel.image.asyncPainterResource
import io.kamel.image.config.Default
import io.kamel.image.config.LocalKamelConfig

/**
 * A composable that displays a user avatar based on their email address (uses Gravatar).
 *
 * @param imageUrl The URL of the user's avatar image.
 * @param onClick The lambda to be invoked when the avatar is clicked.
 */
@Composable
fun UserAvatar(
    imageUrl: String? = null,
    loadingEffect: Boolean = false,
    onClick: () -> Unit
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .clickable { onClick() }
    ) {
        CompositionLocalProvider(LocalKamelConfig provides AvatarKamelConfig) {
            if (!imageUrl.isNullOrEmpty()) {
                KamelImage(
                    resource = { asyncPainterResource(imageUrl) },
                    contentDescription = "用户头像",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(
                            BorderStroke(
                                2.dp,
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
                            ),
                            shape = CircleShape
                        ),
                    contentScale = ContentScale.Crop,
                    onLoading = {
                        if (loadingEffect) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    onFailure = {
                        DefaultAvatar(onClick = onClick)
                    }
                )
            } else {
                DefaultAvatar(onClick = onClick)
            }
        }
    }
}


@Composable
fun DefaultAvatar(
    onClick: () -> Unit
) {
    Icon(
        imageVector = Icons.Default.AccountCircle,
        contentDescription = "默认用户头像",
        modifier = Modifier
            .size(80.dp)
            .clip(CircleShape)
            .border(
                BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                shape = CircleShape
            )
            .clickable { onClick() },
        tint = MaterialTheme.colorScheme.primary
    )
}



