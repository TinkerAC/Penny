package app.penny.core.network.clients

import app.penny.core.data.kvstore.TokenProvider
import app.penny.core.network.BaseAuthedApiClient
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import io.ktor.client.HttpClient
import io.ktor.http.HttpMethod
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject



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
