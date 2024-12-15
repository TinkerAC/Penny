package app.penny.presentation.ui

import app.penny.feature.setting.Language
import dev.icerock.moko.resources.desc.StringDesc

object LanguageManager {
    fun setLocaleTo(language: Language) {
        StringDesc.localeType = StringDesc.LocaleType.Custom(language.locale)
    }

    fun resetLocaleToSystem() {
        StringDesc.localeType = StringDesc.LocaleType.System
    }

}