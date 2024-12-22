package app.penny.core.domain.enum

import app.penny.shared.SharedRes
import dev.icerock.moko.resources.ImageResource

enum class Currency(
    val currencyCode: String,
    val currencyName: String,
    val currencySymbol: String,
    val regionFlag: ImageResource

) {
    USD("USD", "United States Dollar", "$", SharedRes.images.flag_usa),
    EUR("EUR", "Euro", "€", SharedRes.images.flag_eu),
    JPY("JPY", "Japanese Yen", "¥", SharedRes.images.flag_jp),
    CNY("CNY", "Chinese Yuan", "¥", SharedRes.images.flag_cn),
    RUB("RUB", "Russian Ruble", "₽", SharedRes.images.flag_ru),

}