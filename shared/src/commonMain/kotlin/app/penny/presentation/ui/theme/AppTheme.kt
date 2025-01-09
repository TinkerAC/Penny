package app.penny.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import app.penny.presentation.enumerate.AppThemeContrast
import app.penny.presentation.enumerate.AppDisplayMode
import app.penny.presentation.ui.theme.theme1.AppTypography
import com.materialkolor.DynamicMaterialTheme

@Composable
fun AppTheme(
    appTheme: AppTheme,
    appThemeContrast: AppThemeContrast,
    appDisplayMode: AppDisplayMode,
    content: @Composable () -> Unit
) {
    val useDarkTheme = when (appDisplayMode) {
        AppDisplayMode.SYSTEM -> isSystemInDarkTheme()
        AppDisplayMode.LIGHT -> false
        AppDisplayMode.DARK -> true
    }


    when (
        appTheme
    ) {
        is AppTheme.Static -> {
            val colorScheme = when (appThemeContrast) {
                AppThemeContrast.HIGH -> {
                    when (useDarkTheme) {
                        true -> appTheme.highContrastDarkColorTheme
                        false -> appTheme.highContrastLightColorTheme
                    }
                }

                AppThemeContrast.MEDIUM -> {
                    when (useDarkTheme) {
                        true -> appTheme.mediumContrastDarkColorTheme
                        false -> appTheme.mediumContrastLightColorTheme
                    }
                }

                AppThemeContrast.LOW -> {
                    when (useDarkTheme) {
                        true -> appTheme.darkTheme
                        false -> appTheme.lightTheme
                    }
                }
            }
            MaterialTheme(
                colorScheme = colorScheme,
                typography = AppTypography,
                content = content
            )
        }

        is AppTheme.DynamicAppTheme -> {
            DynamicMaterialTheme(
                seedColor = appTheme.seedColor,
                useDarkTheme = useDarkTheme,
                animate = true,
                content = content
            )
        }
    }

}




