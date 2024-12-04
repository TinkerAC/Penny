package app.penny.presentation.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.Composable


val LocalSpacing = staticCompositionLocalOf { Spacing() }

data class Spacing(
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp
)


val MaterialTheme.spacing: Spacing
    @Composable
    get() = LocalSpacing.current
