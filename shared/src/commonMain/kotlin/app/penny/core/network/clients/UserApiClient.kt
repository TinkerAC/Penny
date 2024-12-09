package app.penny.core.network.clients

import app.penny.config.Config.API_URL
import app.penny.core.data.kvstore.TokenProvider
import app.penny.core.network.BaseApiClient
import app.penny.servershared.dto.requestDto.LoginRequest
import app.penny.servershared.dto.requestDto.RegisterRequest
import app.penny.servershared.dto.responseDto.CheckIsEmailRegisteredResponse
import app.penny.servershared.dto.responseDto.LoginResponse
import app.penny.servershared.dto.responseDto.RegisterResponse
import io.ktor.client.HttpClient
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.client.request.*
import io.ktor.http.contentType

class UserApiClient(
    httpClient: HttpClient,
    tokenProvider: TokenProvider
) : BaseApiClient(httpClient) {

    suspend fun register(uuid:String? =null,email: String, password: String): RegisterResponse {
        return makeRequest(
            url = "$API_URL/user/register",
            method = HttpMethod.Post
        ) {
            contentType(ContentType.Application.Json)
            setBody(RegisterRequest(
                uuid = uuid
                , email = email, password = password
            ))
        }
    }

    suspend fun login(email: String, password: String): LoginResponse {
        return makeRequest(
            url = "$API_URL/user/login",
            method = HttpMethod.Post
        ) {
            contentType(ContentType.Application.Json)
            setBody(LoginRequest(email = email, password = password))
        }
    }

    suspend fun checkIsEmailRegistered(email: String): CheckIsEmailRegisteredResponse {
        val response = makeRequest<CheckIsEmailRegisteredResponse>(
            url = "$API_URL/user/checkIsEmailRegistered",
            method = HttpMethod.Get
        ) {
            parameter("email", email)
        }

        return response
    }
}