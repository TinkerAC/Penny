package app.penny.core.network.clients

import app.penny.config.Config.API_URL
import app.penny.core.data.kvstore.TokenProvider
import app.penny.core.network.BaseAuthedApiClient
import app.penny.servershared.dto.GetActionRequest
import io.ktor.client.HttpClient
import io.ktor.client.request.setBody
import io.ktor.client.utils.EmptyContent.contentType
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.http.contentType


class AiApiClient(
    httpClient: HttpClient,
    tokenProvider: TokenProvider
) : BaseAuthedApiClient(httpClient, tokenProvider) {

    suspend fun getAction(
        text: String
    ): String {
        return makeAuthenticatedRequest(
            url = "$API_URL/ai/get-action",
            method = HttpMethod.Post
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                GetActionRequest(
                    text = text
                )
            )

        }
    }


}