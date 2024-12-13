package app.penny.presentation.ui.theme

import androidx.compose.material3.ColorScheme
import app.penny.presentation.ui.theme.theme1.Theme1
import app.penny.presentation.ui.theme.theme2.Theme2

abstract class ThemeConfig(
) {
    abstract val name: String
    abstract val lightTheme: ColorScheme
    abstract val mediumContrastLightColorTheme: ColorScheme
    abstract val highContrastLightColorTheme: ColorScheme
    abstract val darkTheme: ColorScheme
    abstract val mediumContrastDarkColorTheme: ColorScheme
    abstract val highContrastDarkColorTheme: ColorScheme


}




