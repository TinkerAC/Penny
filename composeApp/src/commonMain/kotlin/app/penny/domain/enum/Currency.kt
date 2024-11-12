package app.penny.domain.enum

import org.jetbrains.compose.resources.DrawableResource
import penny.composeapp.generated.resources.Res
import penny.composeapp.generated.resources.flag_cn
import penny.composeapp.generated.resources.flag_eu
import penny.composeapp.generated.resources.flag_jp
import penny.composeapp.generated.resources.flag_ru
import penny.composeapp.generated.resources.flag_usa


enum class Currency(
    val currencyCode: String,
    val currencyName: String,
    val currencySymbol: String,
    val regionFlag: DrawableResource

) {
    USD("USD", "United States Dollar", "$", Res.drawable.flag_usa),
    EUR("EUR", "Euro", "€", Res.drawable.flag_eu),
    JPY("JPY", "Japanese Yen", "¥", Res.drawable.flag_jp),
    CNY("CNY", "Chinese Yuan", "¥", Res.drawable.flag_cn),
    RUB("RUB", "Russian Ruble", "₽", Res.drawable.flag_ru),


}