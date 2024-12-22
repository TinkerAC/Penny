package app.penny.core.domain.enum

import app.penny.shared.SharedRes
import dev.icerock.moko.resources.StringResource

/**
 * The contrast of the app appTheme.
 */
enum class AppThemeContrast(
    val displayName: StringResource
) {
    HIGH(
        SharedRes.strings.constraint_high
    ),
    MEDIUM(
        SharedRes.strings.constraint_medium
    ),
    LOW(
        SharedRes.strings.constraint_low
    )
}