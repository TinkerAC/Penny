package app.penny.feature.currencyConvater

import app.penny.core.domain.enumerate.Currency

/**
 * 存储当前货币转换所需的 UI 状态
 *
 * @param amount 用户在第一个卡片输入的金额
 * @param fromCurrency 当前源货币
 * @param toCurrency 当前目标货币
 * @param result 计算得出的转换结果
 * @param currentRate 当前源货币到目标货币的汇率信息，用于展示 1 fromCurrency = x toCurrency
 */
data class CurrencyConverterUiState(
    val amount: String = "",              //  the amount of the first card
    val fromCurrency: Currency = Currency.USD,
    val toCurrency: Currency = Currency.EUR,
    val result: String = "",              // the result of the second card
    val currentRate: String = ""       // used to show 1 fromCurrency = x toCurrency

)
