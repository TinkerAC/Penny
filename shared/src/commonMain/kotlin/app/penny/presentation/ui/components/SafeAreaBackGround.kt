// SafeAreaBackgrounds.kt
package app.penny.presentation.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color

@Composable
fun SafeAreaBackgrounds(
    topColor: Color,
    bottomColor: Color,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                val upperHeight = size.height * 0.5f
                // 绘制上半部分背景
                drawRect(
                    color = topColor,
                    size = Size(size.width, upperHeight),
                    topLeft = Offset.Zero
                )
                // 绘制下半部分背景
                drawRect(
                    color = bottomColor,
                    size = Size(size.width, size.height - upperHeight),
                    topLeft = Offset(0f, upperHeight)
                )
            }.windowInsetsPadding(
                insets = WindowInsets.safeDrawing
            )

    ) {
        content()
    }
}