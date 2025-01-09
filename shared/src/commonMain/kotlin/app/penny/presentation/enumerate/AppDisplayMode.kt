package app.penny.presentation.enumerate

import app.penny.shared.SharedRes
import dev.icerock.moko.resources.StringResource

enum class AppDisplayMode(
    val displayName: StringResource
) {
    SYSTEM(
        displayName = SharedRes.strings.display_mode_system
    ),
    LIGHT(
        displayName = SharedRes.strings.display_mode_light
    ),
    DARK(
        displayName = SharedRes.strings.display_mode_dark
    )
}