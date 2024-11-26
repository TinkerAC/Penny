// file: shared/src/commonMain/kotlin/app/penny/core/network/BaseApiClient.kt
package app.penny.core.network

import app.penny.core.data.kvstore.TokenProvider
import co.touchlab.kermit.Logger
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.HttpRequestBuilder
import io.ktor.client.request.header
import io.ktor.client.request.request
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpMethod

abstract class BaseApiClient(
    val httpClient: HttpClient,
) {
    suspend inline fun <reified T> makeRequest(
        url: String,
        method: HttpMethod,
        noinline setup: HttpRequestBuilder.() -> Unit = {}
    ): T {
        Logger.d { "Making $method request to $url" }
        return try {
            val response: HttpResponse = httpClient.request(url) {
                this.method = method
                setup()
            }
            val responseBody: T = response.body()
            Logger.d {
                "Request to $url succeeded with:" +
                        " statusCode: ${response.status.value}," +
                        " body: $responseBody"
            }
            responseBody
        } catch (e: Exception) {
            Logger.e(e) { "Request to $url failed with exception: $e" }
            throw e
        }
    }

}


abstract class BaseAuthedApiClient(
    httpClient: HttpClient,
    val tokenProvider: TokenProvider
) : BaseApiClient(httpClient) {

    suspend inline fun <reified T> makeAuthenticatedRequest(
        url: String,
        method: HttpMethod,
        noinline setup: HttpRequestBuilder.() -> Unit = {}
    ): T {
        Logger.d { "Making authenticated $method request to $url" }
        return try {
            val response: HttpResponse = httpClient.request(url) {
                this.method = method
                setup()
                header("Authorization", "Bearer ${tokenProvider.getAccessToken()}")
            }
            val responseBody: T = response.body()
            Logger.d {
                "Authenticated request to $url succeeded with:" +
                        " statusCode: ${response.status.value}," +
                        " body: $responseBody"
            }
            responseBody
        } catch (e: Exception) {
            Logger.e(e) { "Authenticated request to $url failed with exception: $e" }
            throw e
        }
    }

}