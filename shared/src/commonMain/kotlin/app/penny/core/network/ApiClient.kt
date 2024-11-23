package app.penny.core.network

import app.penny.config.Config.API_URL
import app.penny.core.network.dto.LoginResponseDto
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.PushLedgersRequest
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.serialization.json.Json


class ApiClient(private val httpClient: HttpClient) {

    suspend fun register(username: String, password: String): String {
        val response: String = httpClient.post(
            "$API_URL/user/register"
        ) {
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to username, "password" to password))
        }.body()
        return response
    }

    suspend fun login(username: String, password: String): LoginResponseDto {
        val response: String = httpClient.post(
            "$API_URL/user/login"
        ) {
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to username, "password" to password))
        }.body()
        return Json.decodeFromString(
            LoginResponseDto.serializer(),
            response
        )
    }

    suspend fun checkIsUsernameValid(username: String): Boolean {
        val response: HttpResponse = httpClient.get(
            "$API_URL/user/checkIsUsernameValid"
        ) {
            parameter("username", username)
        }
        return response.status == HttpStatusCode.OK

    }


    suspend fun pushLedgers(ledgers: List<LedgerDto>) {
        httpClient.post(
            "$API_URL/sync/ledger/upload"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                PushLedgersRequest(
                    total = ledgers.size,
                    ledgers = ledgers
                )
            )
        }
    }


}