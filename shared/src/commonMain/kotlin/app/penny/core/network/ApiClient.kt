package app.penny.core.network

import app.penny.config.Config.API_URL
import app.penny.servershared.dto.CheckIsEmailRegisteredRequest
import app.penny.servershared.dto.CheckIsEmailRegisteredResponse
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.LoginRequest
import app.penny.servershared.dto.LoginResponse
import app.penny.servershared.dto.RegisterRequest
import app.penny.servershared.dto.RegisterResponse
import app.penny.servershared.dto.UploadLedgerRequest
import app.penny.servershared.dto.UploadLedgerResponse
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


class ApiClient(private val httpClient: HttpClient) {

    suspend fun register(email: String, password: String): String {
        try {
            val response: RegisterResponse = httpClient.post(
                "$API_URL/user/checkIsEmailRegistered"
            ) {
                setBody(
                    RegisterRequest(
                        email = email,
                        password = password
                    )
                )
            }.body()

            if (response.success) {
                return "User already registered"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
        return "User registered successfully"
    }


    suspend fun login(email: String, password: String, username: String?): LoginResponse {
        val response: LoginResponse = httpClient.post(
            "$API_URL/user/login"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                LoginRequest(
                    email = email,
                    username = null,
                    password = password
                )
            )
        }.body()
        return response
    }

    suspend fun checkIsEmailRegistered(username: String): Boolean {
        val response: CheckIsEmailRegisteredResponse = httpClient.get(
            "$API_URL/user/checkIsEmailRegistered"
        ) {
            parameter("email", username)
        }.body()

        return response.isEmailRegistered

    }


    suspend fun pushLedgers(ledgers: List<LedgerDto>, lastSynced: Long): UploadLedgerResponse {

        return httpClient.post(
            "$API_URL/sync/ledger/upload"
        ) {
            contentType(ContentType.Application.Json)
            setBody(
                UploadLedgerRequest(
                    total = ledgers.size,
                    ledgers = ledgers,
                    lastSyncedAt = lastSynced
                )
            )
        }.body()
    }


}