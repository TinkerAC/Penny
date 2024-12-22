package app.penny.presentation.ui

import androidx.compose.ui.text.intl.Locale
import app.penny.core.data.repository.UserPreferenceRepository
import app.penny.core.domain.enum.Language
import app.penny.di.getKoinInstance
import dev.icerock.moko.resources.desc.StringDesc

object LocaleManager {

    private val userPreferenceRepository = getKoinInstance<UserPreferenceRepository>()
    public var currentLocale: String = userPreferenceRepository.getLanguage().locale

    fun setLocaleTo(language: Language) {
        this.currentLocale = language.locale
        StringDesc.localeType = StringDesc.LocaleType.Custom(language.locale)
    }

    fun resetLocaleToSystem() {
        StringDesc.localeType = StringDesc.LocaleType.System
    }

}