package app.penny.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import app.penny.presentation.ui.theme.theme1.AppTypography
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import app.penny.presentation.ui.theme.theme1.Theme1
import app.penny.presentation.ui.theme.theme2.Theme2


@Composable
fun AppTheme(
    themeConfig: ThemeConfig,
    darkTheme: DisplayMode,
    constraints: ThemeConstraint,
    content: @Composable() () -> Unit
) {
    val useDarkTheme: Boolean = when (darkTheme) {
        DisplayMode.SYSTEM -> isSystemInDarkTheme()
        DisplayMode.LIGHT -> false
        DisplayMode.DARK -> true
    }

    val colorScheme = when (useDarkTheme) {
        true -> when (constraints) {
            ThemeConstraint.HIGH -> themeConfig.highContrastDarkColorTheme
            ThemeConstraint.MEDIUM -> themeConfig.mediumContrastDarkColorTheme
            ThemeConstraint.LOW -> themeConfig.darkTheme
        }

        false -> when (constraints) {
            ThemeConstraint.HIGH -> themeConfig.highContrastLightColorTheme
            ThemeConstraint.MEDIUM -> themeConfig.mediumContrastLightColorTheme
            ThemeConstraint.LOW -> themeConfig.lightTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

enum class ThemeConstraint {
    HIGH,
    MEDIUM,
    LOW
}


enum class DisplayMode {
    SYSTEM,
    LIGHT,
    DARK
}


enum class ThemeColor(
    val themeConfig: ThemeConfig
) {
    THEME1(Theme1),
    THEME2(Theme2);

}
