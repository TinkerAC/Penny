package app.penny.feature.currencyConvater

import app.penny.core.domain.enumerate.Currency
import app.penny.core.network.clients.ThirdPartyApiClient
import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import com.ionspin.kotlin.bignum.decimal.DecimalMode
import com.ionspin.kotlin.bignum.decimal.RoundingMode
import com.ionspin.kotlin.bignum.decimal.toBigDecimal
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CurrencyConverterViewModel(
    private val thirdPartyApiClient: ThirdPartyApiClient
) : ScreenModel {

    // 模拟第三方请求，返回汇率Map
    private suspend fun fetchRates(): Map<String, BigDecimal> {
        return thirdPartyApiClient.getSupportedCurrencyMap()

    }

    // 支持的货币列表
    val availableCurrencies: List<Currency> = Currency.supportedCurrencies

    // UI 状态
    private val _uiState = MutableStateFlow(CurrencyConverterUiState())
    val uiState: StateFlow<CurrencyConverterUiState> = _uiState

    // 汇率表
    private var rates: Map<String, BigDecimal> = emptyMap()
    private val decimalMode = DecimalMode(
        decimalPrecision = 50,
        roundingMode = RoundingMode.ROUND_HALF_TO_EVEN,
        scale = 6
    )

    init {
        // 异步获取汇率
        screenModelScope.launch {
            rates = fetchRates()
            calculateRate(_uiState.value.fromCurrency, _uiState.value.toCurrency)
        }
    }

    /**
     * 当用户在第一个卡片输入金额时（源货币 -> 目标货币）
     */
    fun onAmountChanged(newAmount: String) {
        _uiState.update { current -> current.copy(amount = newAmount) }
        calculateResult(newAmount, _uiState.value.fromCurrency, _uiState.value.toCurrency)
    }

    /**
     * 当用户在第二个卡片输入金额时（目标货币 -> 源货币，反向转换）
     */
    fun onResultChanged(newResult: String) {
        _uiState.update { current -> current.copy(result = newResult) }
        calculateReverse(newResult, _uiState.value.fromCurrency, _uiState.value.toCurrency)
    }

    /**
     * 用户更改源货币
     */
    fun onFromCurrencyChanged(newCurrency: Currency) {
        _uiState.update { current -> current.copy(fromCurrency = newCurrency) }
        calculateRate(newCurrency, _uiState.value.toCurrency)
        calculateResult(_uiState.value.amount, newCurrency, _uiState.value.toCurrency)
    }

    /**
     * 用户更改目标货币
     */
    fun onToCurrencyChanged(newCurrency: Currency) {
        _uiState.update { current -> current.copy(toCurrency = newCurrency) }
        calculateRate(_uiState.value.fromCurrency, newCurrency)
        calculateResult(_uiState.value.amount, _uiState.value.fromCurrency, newCurrency)
    }

    /**
     * 交换两种货币
     */
    fun onSwapCurrencies() {
        _uiState.update { current ->
            current.copy(
                fromCurrency = current.toCurrency,
                toCurrency = current.fromCurrency
            )
        }
        calculateRate(_uiState.value.fromCurrency, _uiState.value.toCurrency)
        // 再计算一次
        calculateResult(
            _uiState.value.amount,
            _uiState.value.fromCurrency,
            _uiState.value.toCurrency
        )
    }

    private fun calculateRate(fromCurrency: Currency, toCurrency: Currency) {
        val fromRate = rates[fromCurrency.code] ?: BigDecimal.ONE
        val toRate = rates[toCurrency.code] ?: BigDecimal.ONE
        val currentRateBD = toRate.divide(fromRate, decimalMode)
        val currentRateStr = currentRateBD.toPlainString()
        _uiState.update { current -> current.copy(currentRate = currentRateStr) }
    }

    /**
     * 正向计算：
     * 用户在 "from" 卡片输入金额 -> 转成 USD -> 转成目标货币
     */
    private fun calculateResult(amountStr: String, fromCurrency: Currency, toCurrency: Currency) {
        if (rates.isEmpty()) return
        val amountBD = runCatching { amountStr.toBigDecimal() }.getOrNull() ?: BigDecimal.ZERO


        val fromRate = rates[fromCurrency.code] ?: BigDecimal.ONE
        val toRate = rates[toCurrency.code] ?: BigDecimal.ONE

        // 先把本地货币换成 USD
        val amountInUSD = amountBD.divide(fromRate, decimalMode)
        // 再从 USD 转到目标货币
        val resultBD = amountInUSD.multiply(toRate)

        // 计算 "1 from = x to"
        val currentRateBD = toRate.divide(fromRate, decimalMode)
        val currentRateStr = currentRateBD.toPlainString()

        // 更新 UI
        _uiState.update { current ->
            current.copy(
                result = resultBD.divide(BigDecimal.ONE, decimalMode).toPlainString(),
                currentRate = currentRateStr
            )
        }
    }

    /**
     * 反向计算：
     * 用户在 "to" 卡片输入金额 -> 转成 USD -> 转成源货币
     */
    private fun calculateReverse(resultStr: String, fromCurrency: Currency, toCurrency: Currency) {
        if (rates.isEmpty()) return
        val resultBD = runCatching { resultStr.toBigDecimal() }.getOrNull() ?: BigDecimal.ZERO

        val fromRate = rates[fromCurrency.code] ?: BigDecimal.ONE
        val toRate = rates[toCurrency.code] ?: BigDecimal.ONE

        // 先把目标货币换成 USD
        val amountInUSD = resultBD.divide(toRate, decimalMode)
        // 再从 USD 转到源货币
        val fromBD = amountInUSD.multiply(fromRate)

        // 计算 "1 from = x to"
        val currentRateBD = toRate.divide(fromRate, decimalMode)
        val currentRateStr = currentRateBD.toPlainString()

        // 更新 UI
        _uiState.update { current ->
            current.copy(
                amount = fromBD.divide(BigDecimal.ONE, decimalMode).toPlainString(),
                currentRate = currentRateStr
            )
        }
    }
}
