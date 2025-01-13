package app.penny.core.network.clients

import app.penny.core.data.kvstore.TokenProvider
import app.penny.core.network.BaseAuthedApiClient
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject

//
//{
//    "result": "success",
//    "provider": "https://www.exchangerate-api.com",
//    "documentation": "https://www.exchangerate-api.com/docs/free",
//    "terms_of_use": "https://www.exchangerate-api.com/terms",
//    "time_last_update_unix": 1736726552,
//    "time_last_update_utc": "Mon, 13 Jan 2025 00:02:32 +0000",
//    "time_next_update_unix": 1736814062,
//    "time_next_update_utc": "Tue, 14 Jan 2025 00:21:02 +0000",
//    "time_eol_unix": 0,
//    "base_code": "USD",
//    "rates": {
//    "USD": 1,
//    "AED": 3.6725,
//    "AFN": 71.69067,
//    "ALL": 95.597759,
//    "AMD": 399.438281,
//    "ANG": 1.79,
//    "AOA": 921.196147,
//    "ARS": 1039.75,
//    "AUD": 1.624102,
//    "AWG": 1.79,

class ThirdPartyApiClient(
    httpClient: HttpClient,
    tokenProvider: TokenProvider
) : BaseAuthedApiClient(httpClient, tokenProvider) {

    suspend fun getSupportedCurrencyMap(): Map<String, BigDecimal> {
        val response: JsonObject = makeAuthenticatedRequest(
            url = "https://open.er-api.com/v6/latest/USD",
            method = HttpMethod.Get
        )
        val rates = response["rates"]?.jsonObject
        return rates?.mapValues { (_, value) ->
            BigDecimal.parseString(value.toString())
        } ?: emptyMap()
    }

}
