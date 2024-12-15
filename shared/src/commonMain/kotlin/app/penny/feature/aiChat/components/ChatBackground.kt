package app.penny.feature.aiChat.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import org.jetbrains.compose.resources.painterResource

@Composable
fun ChatBackground(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primaryContainer,
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
//        // 可选：添加图案或图片
//        Image(
//            painter = painterResource(id = R.drawable.chat_pattern), // 替换为您的图案图片
//            contentDescription = "Chat Background Pattern",
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(16.dp)
//                .alpha(0.1f) // 根据需要调整透明度
//        )
    }
}
