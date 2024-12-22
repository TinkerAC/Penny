package app.penny.core.domain.enum


/**
 * Enum class for supported languages in the app.
 */
enum class Language(
    val locale: String,
    val displayName: String
) {
    ENGLISH(
        "en",
        "English"
    ),
    CHINESE(
        "zh",
        "中文"
    );
}