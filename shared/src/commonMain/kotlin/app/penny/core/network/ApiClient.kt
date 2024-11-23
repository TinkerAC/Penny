package app.penny.core.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import app.penny.config.Config.API_URL
import app.penny.core.network.dto.LoginResponseDto
import io.ktor.client.statement.HttpResponse
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


}