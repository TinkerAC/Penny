package app.penny.core.network.clients

import app.penny.config.Config.API_URL
import app.penny.core.network.BaseApiClient
import app.penny.servershared.dto.requestDto.RefreshTokenRequest
import app.penny.servershared.dto.responseDto.RefreshTokenResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType

class AuthApiClient(
    httpClient: HttpClient,
) : BaseApiClient(httpClient) {

    suspend fun refreshAccessToken(refreshToken: String): RefreshTokenResponse {
        return makeRequest(
            url = "$API_URL/auth/refresh",
            method = HttpMethod.Post
        ) {
            contentType(ContentType.Application.Json)
            setBody(RefreshTokenRequest(refreshToken = refreshToken))
        }
    }
}