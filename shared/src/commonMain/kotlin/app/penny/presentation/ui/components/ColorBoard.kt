package app.penny.presentation.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun DisplayColorScheme() {
    val colorScheme = MaterialTheme.colorScheme

    // 手动列出 ColorScheme 中所有的颜色属性
    val colors = listOf(
        ColorItem("primary", colorScheme.primary),
        ColorItem("onPrimary", colorScheme.onPrimary),
        ColorItem("primaryContainer", colorScheme.primaryContainer),
        ColorItem("onPrimaryContainer", colorScheme.onPrimaryContainer),
        ColorItem("secondary", colorScheme.secondary),
        ColorItem("onSecondary", colorScheme.onSecondary),
        ColorItem("secondaryContainer", colorScheme.secondaryContainer),
        ColorItem("onSecondaryContainer", colorScheme.onSecondaryContainer),
        ColorItem("tertiary", colorScheme.tertiary),
        ColorItem("onTertiary", colorScheme.onTertiary),
        ColorItem("tertiaryContainer", colorScheme.tertiaryContainer),
        ColorItem("onTertiaryContainer", colorScheme.onTertiaryContainer),
        ColorItem("error", colorScheme.error),
        ColorItem("onError", colorScheme.onError),
        ColorItem("errorContainer", colorScheme.errorContainer),
        ColorItem("onErrorContainer", colorScheme.onErrorContainer),
        ColorItem("background", colorScheme.background),
        ColorItem("onBackground", colorScheme.onBackground),
        ColorItem("surface", colorScheme.surface),
        ColorItem("onSurface", colorScheme.onSurface),
        ColorItem("surfaceVariant", colorScheme.surfaceVariant),
        ColorItem("onSurfaceVariant", colorScheme.onSurfaceVariant),
        ColorItem("outline", colorScheme.outline),
        ColorItem("inverseOnSurface", colorScheme.inverseOnSurface),
        ColorItem("inverseSurface", colorScheme.inverseSurface),
        ColorItem("inversePrimary", colorScheme.inversePrimary),
//        ColorItem("shadow", colorScheme.shadow),
        ColorItem("surfaceTint", colorScheme.surfaceTint),
        ColorItem("outlineVariant", colorScheme.outlineVariant),
        ColorItem("scrim", colorScheme.scrim)
    )

    // 使用 Column 来垂直展示所有颜色
    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
    ) {
        colors.forEach { colorItem ->
            ColorDisplayItem(name = colorItem.name, color = colorItem.color)
            Spacer(modifier = Modifier.height(8.dp)) // 添加间隔
        }
    }
}

data class ColorItem(val name: String, val color: Color)

@Composable
fun ColorDisplayItem(name: String, color: Color) {
    // 显示颜色和文本
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .background(color)
            .padding(16.dp),
        color = color,
        tonalElevation = 4.dp
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
        ) {
            // 显示颜色的名称
            BasicText(
                text = name,
                style = TextStyle(
                    fontSize = 16.sp,
                    color = contentColorFor(backgroundColor = color)
                )
            )

            // 显示颜色的十六进制值
            BasicText(
                text = "#${color.value.toUInt().toString(16).uppercase().padStart(8, '0')}",
                style = TextStyle(
                    fontSize = 12.sp,
                    color = contentColorFor(backgroundColor = color)
                )
            )
        }
    }
}

@Composable
fun contentColorFor(backgroundColor: Color): Color {
    return if (backgroundColor.luminance() > 0.5) Color.Black else Color.White
}

