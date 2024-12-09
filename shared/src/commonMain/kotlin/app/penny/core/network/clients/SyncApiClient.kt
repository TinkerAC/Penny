package app.penny.core.network.clients

import app.penny.config.Config.API_URL
import app.penny.core.data.kvstore.TokenProvider
import app.penny.core.network.BaseAuthedApiClient
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.TransactionDto
import app.penny.servershared.dto.requestDto.UploadLedgerRequest
import app.penny.servershared.dto.requestDto.UploadTransactionRequest
import app.penny.servershared.dto.responseDto.DownloadLedgerResponse
import app.penny.servershared.dto.responseDto.DownloadTransactionResponse
import app.penny.servershared.dto.responseDto.RemoteUnsyncedDataCountResponse
import app.penny.servershared.dto.responseDto.UploadLedgerResponse
import app.penny.servershared.dto.responseDto.UploadTransactionResponse
import io.ktor.client.HttpClient
import io.ktor.http.ContentType
import io.ktor.http.HttpMethod
import io.ktor.client.request.*
import io.ktor.http.contentType

class SyncApiClient(
    httpClient: HttpClient,
    tokenProvider: TokenProvider
) : BaseAuthedApiClient(httpClient, tokenProvider) {

    suspend fun uploadLedgers(ledgers: List<LedgerDto>, lastSynced: Long): UploadLedgerResponse {
        return makeAuthenticatedRequest(
            url = "$API_URL/sync/ledger/upload",
            method = HttpMethod.Post
        ) {
            contentType(ContentType.Application.Json)
            setBody(UploadLedgerRequest(
                total = ledgers.size,
                ledgers = ledgers,
                lastSyncedAt = lastSynced
            ))
        }
    }

    suspend fun downloadLedgers(lastSyncedAt: Long): DownloadLedgerResponse {
        return makeAuthenticatedRequest(
            url = "$API_URL/sync/ledger/download",
            method = HttpMethod.Get
        ) {
            parameter("lastSyncedAt", lastSyncedAt)
        }
    }

    suspend fun uploadTransactions(
        transactions: List<TransactionDto>,
        lastSynced: Long
    ): UploadTransactionResponse {
        return makeAuthenticatedRequest(
            url = "$API_URL/sync/transaction/upload",
            method = HttpMethod.Post
        ) {
            contentType(ContentType.Application.Json)
            setBody(UploadTransactionRequest(transactions = transactions, lastSynced = lastSynced))
        }
    }


    suspend fun downloadTransactions(lastSyncedAt: Long): DownloadTransactionResponse {
        return makeAuthenticatedRequest(
            url = "$API_URL/sync/transaction/download",
            method = HttpMethod.Get
        ) {
            parameter("lastSyncedAt", lastSyncedAt)
        }
    }


    suspend fun getRemoteUnsyncedDataCount(lastSyncedAt: Long): RemoteUnsyncedDataCountResponse {
        return makeAuthenticatedRequest(
            url = "$API_URL/sync/count",
            method = HttpMethod.Get
        ) {
            parameter("lastSyncedAt", lastSyncedAt)
        }

    }


}