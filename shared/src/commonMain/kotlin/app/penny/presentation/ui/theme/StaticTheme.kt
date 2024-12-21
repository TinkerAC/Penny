package app.penny.presentation.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color
import app.penny.presentation.ui.theme.theme1.darkScheme
import app.penny.presentation.ui.theme.theme1.highContrastDarkColorScheme
import app.penny.presentation.ui.theme.theme1.highContrastLightColorScheme
import app.penny.presentation.ui.theme.theme1.lightScheme
import app.penny.presentation.ui.theme.theme1.mediumContrastDarkColorScheme
import app.penny.presentation.ui.theme.theme1.mediumContrastLightColorScheme
import app.penny.presentation.ui.theme.theme1.unspecified_scheme
import app.penny.shared.SharedRes
import com.materialkolor.ktx.toHex
import dev.icerock.moko.resources.StringResource
import kotlinx.datetime.Clock
import kotlin.random.Random

sealed class AppTheme {
    abstract val primaryColor: Color
    abstract val nameStringResource: StringResource


    sealed class Static : AppTheme() {
        abstract val persistentName: String
        abstract val lightTheme: ColorScheme
        abstract val mediumContrastLightColorTheme: ColorScheme
        abstract val highContrastLightColorTheme: ColorScheme
        abstract val darkTheme: ColorScheme
        abstract val mediumContrastDarkColorTheme: ColorScheme
        abstract val highContrastDarkColorTheme: ColorScheme
        abstract val unSpecifiedColorScheme: ColorFamily

        companion object {
            fun fromPersistentName(persistentName: String): Static {
                return when (persistentName) {
                    TerracottaRed.persistentName -> TerracottaRed
                    AppleGreen.persistentName -> AppleGreen
                    SkyBlue.persistentName -> SkyBlue
                    LemonYellow.persistentName -> LemonYellow
                    else -> TerracottaRed
                }
            }


            val items = listOf(
                TerracottaRed,
                AppleGreen,
                SkyBlue,
                LemonYellow
            )
        }


        data object TerracottaRed : Static() {
            override val primaryColor: Color = Color(0xFFB33B15)
            override val persistentName = "StaticTheme_#B33B15"
            override val nameStringResource = SharedRes.strings.terracotta_red_theme
            override val lightTheme = lightScheme
            override val mediumContrastLightColorTheme = mediumContrastLightColorScheme
            override val highContrastLightColorTheme = highContrastLightColorScheme
            override val darkTheme = darkScheme
            override val mediumContrastDarkColorTheme: ColorScheme = mediumContrastDarkColorScheme
            override val highContrastDarkColorTheme = highContrastDarkColorScheme
            override val unSpecifiedColorScheme = unspecified_scheme
        }

        data object AppleGreen : Static() {
            override val primaryColor: Color = Color(0xFF63A002)
            override val persistentName = "StaticTheme_${primaryColor.toHex()}"
            override val nameStringResource = SharedRes.strings.apple_green_theme
            override val lightTheme = app.penny.presentation.ui.theme.theme2.lightScheme
            override val mediumContrastLightColorTheme: ColorScheme =
                app.penny.presentation.ui.theme.theme2.mediumContrastLightColorScheme
            override val highContrastLightColorTheme: ColorScheme =
                app.penny.presentation.ui.theme.theme2.highContrastLightColorScheme
            override val darkTheme = app.penny.presentation.ui.theme.theme2.darkScheme
            override val mediumContrastDarkColorTheme: ColorScheme =
                app.penny.presentation.ui.theme.theme2.mediumContrastDarkColorScheme
            override val highContrastDarkColorTheme: ColorScheme =
                app.penny.presentation.ui.theme.theme2.highContrastDarkColorScheme
            override val unSpecifiedColorScheme =
                app.penny.presentation.ui.theme.theme2.unspecified_scheme
        }

        data object SkyBlue : Static() {
            override val primaryColor: Color = Color(0xFF769CDF)
            override val persistentName = "StaticTheme_${primaryColor.toHex()}"
            override val nameStringResource = SharedRes.strings.sky_blue_theme
            override val lightTheme = app.penny.presentation.ui.theme.theme3.lightScheme
            override val mediumContrastLightColorTheme: ColorScheme =
                app.penny.presentation.ui.theme.theme3.mediumContrastLightColorScheme
            override val highContrastLightColorTheme: ColorScheme =
                app.penny.presentation.ui.theme.theme3.highContrastLightColorScheme
            override val darkTheme = app.penny.presentation.ui.theme.theme3.darkScheme
            override val mediumContrastDarkColorTheme: ColorScheme =
                app.penny.presentation.ui.theme.theme3.mediumContrastDarkColorScheme
            override val highContrastDarkColorTheme: ColorScheme =
                app.penny.presentation.ui.theme.theme3.highContrastDarkColorScheme
            override val unSpecifiedColorScheme: ColorFamily =
                app.penny.presentation.ui.theme.theme3.unspecified_scheme

        }

        data object LemonYellow : Static() {
            override val primaryColor: Color = Color(0xFFFFDE3F)
            override val persistentName = "StaticTheme_${primaryColor.toHex()}"
            override val nameStringResource = SharedRes.strings.lemon_yellow_theme
            override val lightTheme = app.penny.presentation.ui.theme.theme4.lightScheme
            override val mediumContrastLightColorTheme: ColorScheme =
                app.penny.presentation.ui.theme.theme4.mediumContrastLightColorScheme
            override val highContrastLightColorTheme: ColorScheme =
                app.penny.presentation.ui.theme.theme4.highContrastLightColorScheme
            override val darkTheme = app.penny.presentation.ui.theme.theme4.darkScheme
            override val mediumContrastDarkColorTheme: ColorScheme =
                app.penny.presentation.ui.theme.theme4.mediumContrastDarkColorScheme
            override val highContrastDarkColorTheme: ColorScheme =
                app.penny.presentation.ui.theme.theme4.highContrastDarkColorScheme
            override val unSpecifiedColorScheme: ColorFamily =
                app.penny.presentation.ui.theme.theme4.unspecified_scheme

        }
    }


    class DynamicAppTheme(
        val seedColor: Color
    ) : AppTheme() {
        override val primaryColor: Color = seedColor
        override val nameStringResource: StringResource = SharedRes.strings.custom_theme
        val name: String = "DynamicTheme_${seedColor.toHex()}"

        companion object {
            fun default(): DynamicAppTheme {
                val random = Random(Clock.System.now().toEpochMilliseconds())
                return DynamicAppTheme(Color(
                    red = random.nextInt(0, 255),
                    green = random.nextInt(0, 255),
                    blue = random.nextInt(0, 255)
                ))
            }
        }
    }
}

@Immutable
data class ColorFamily(
    val color: Color, val onColor: Color, val colorContainer: Color, val onColorContainer: Color
)







