package app.penny.network

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import app.penny.config.Config.API_URL

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

    suspend fun login(username: String, password: String): String {
        val response: String = httpClient.post(
            "$API_URL/user/login"
        ) {
            contentType(ContentType.Application.Json)
            setBody(mapOf("username" to username, "password" to password))
        }.body()
        return response
    }


}