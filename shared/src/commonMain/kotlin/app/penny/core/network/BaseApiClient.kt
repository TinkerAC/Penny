package app.penny.core.network

import app.penny.core.data.repository.AuthRepository
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
    val authRepository: AuthRepository
) {

    suspend inline fun <reified T> makeRequest(
        url: String,
        method: HttpMethod,
        noinline setup: HttpRequestBuilder.() -> Unit = {}
    ): T {
        Logger.d { "Making $method request to $url" }
        return try {
            // 发送请求并获取响应
            val response: HttpResponse = httpClient.request(url) {
                this.method = method
                setup()
            }

            // 读取响应体并缓存
            val responseBody: T = response.body()

            // 记录状态码和响应体
            Logger.d {
                "Request to $url succeeded with:" +
                        " statusCode: ${response.status.value}," +
                        " body: $responseBody"
            }

            // 返回响应体
            responseBody
        } catch (e: Exception) {
            Logger.e(e) { "Request to $url failed." }
            throw e
        }
    }

    /**
     * 用于受保护 API 的通用请求方法，自动添加 token 到请求头。
     */
    suspend inline fun <reified T> makeAuthenticatedRequest(
        url: String,
        method: HttpMethod,
        noinline setup: HttpRequestBuilder.() -> Unit = {}
    ): T {
        val token = authRepository.getToken() ?: throw IllegalStateException("No token found.")
        Logger.d { "Making authenticated $method request to $url with token: ${token.take(10)}..." }
        return makeRequest(url, method) {
            header("Authorization", token)
            setup() // 调用者自定义请求
        }
    }




}