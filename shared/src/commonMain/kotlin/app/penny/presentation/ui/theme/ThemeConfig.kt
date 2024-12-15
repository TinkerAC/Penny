package app.penny.presentation.ui.theme

import androidx.compose.material3.ColorScheme

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




