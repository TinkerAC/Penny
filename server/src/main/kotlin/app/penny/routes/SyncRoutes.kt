// 文件：server/src/main/kotlin/app/penny/routes/SyncRoutes.kt
package app.penny.routes

import app.penny.servershared.dto.requestDto.UploadLedgerRequest
import app.penny.servershared.dto.requestDto.UploadTransactionRequest
import app.penny.servershared.dto.responseDto.DownloadLedgerResponse
import app.penny.servershared.dto.responseDto.DownloadTransactionResponse
import app.penny.servershared.dto.responseDto.RemoteUnsyncedDataCountResponse
import app.penny.servershared.dto.responseDto.UploadLedgerResponse
import app.penny.servershared.dto.responseDto.UploadTransactionResponse
import app.penny.services.LedgerService
import app.penny.services.StatisticsService
import app.penny.services.TransactionService
import app.penny.utils.getUserId
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.datetime.Clock

fun Route.syncRoutes(
    ledgerService: LedgerService,
    transactionService: TransactionService,
    statisticsService: StatisticsService
) {
    route("/sync") {
        authenticate("access-jwt") {
            route("/ledger") {
                get("/download") {
                    val userId = call.getUserId()
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }

                    val lastSyncedAt = call.parameters["lastSyncedAt"]?.toLong() ?: 0

                    val ledgers = ledgerService.getLedgersByUserIdAfterLastSync(
                        userId = userId,
                        lastSyncedAt = lastSyncedAt
                    )

                    call.respond(
                        DownloadLedgerResponse(
                            success = true,
                            message = "Ledgers downloaded successfully",
                            total = ledgers.size,
                            ledgers = ledgers,
                            lastSyncedAt = Clock.System.now().epochSeconds
                        )
                    )
                }

                post("/upload") {
                    val userId = call.getUserId()
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }

                    val uploadRequest = call.receive<UploadLedgerRequest>()
                    val ledgerDTOs = uploadRequest.ledgers

                    try {

                        ledgerDTOs.forEach {
                            ledgerService.upsertLedgerByUuid(it)
                        }

                        call.respond(
                            UploadLedgerResponse(
                                success = true,
                                message = "Ledgers uploaded successfully",
                                changedLines = ledgerDTOs.size,
                                lastSyncedAt = Clock.System.now().epochSeconds
                            )
                        )
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            UploadLedgerResponse(
                                success = false,
                                changedLines = 0,
                                lastSyncedAt = Clock.System.now().epochSeconds,
                                message = e.message ?: "An error occurred while uploading ledgers"
                            )
                        )
                    }
                }
            }

            route("/transaction") {
                get("/download") {
                    val userId = call.getUserId()
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }

                    val lastSyncedAt = call.parameters["lastSyncedAt"]?.toLong() ?: 0

                    val transactions = transactionService.findUnsyncedTransactions(
                        userId = userId,
                        lastSyncedAt = lastSyncedAt
                    )

                    call.respond(
                        DownloadTransactionResponse(
                            success = true,
                            message = "Transactions downloaded successfully",
                            total = transactions.size,
                            transactions = transactions,
                            lastSyncedAt = Clock.System.now().epochSeconds
                        )
                    )
                }

                post("/upload") {
                    val userId = call.getUserId()
                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }

                    val uploadRequest = call.receive<UploadTransactionRequest>()
                    val transactionDTOs = uploadRequest.transactions

                    try {
                        transactionDTOs.forEach {
                            transactionService.upsertTransactionByUuid(it)
                        }
                        call.respond(
                            UploadTransactionResponse(
                                success = true,
                                message = "Transactions uploaded successfully",
                                changedLines = transactionDTOs.size,
                                lastSyncedAt = Clock.System.now().epochSeconds
                            )
                        )
                    } catch (e: Exception) {
                        call.respond(
                            HttpStatusCode.InternalServerError,
                            UploadTransactionResponse(
                                success = false,
                                changedLines = 0,
                                lastSyncedAt = Clock.System.now().epochSeconds,
                                message = e.message
                                    ?: "An error occurred while uploading transactions"
                            )
                        )
                    }
                }
            }

            get("/count") {
                val userId = call.getUserId()
                if (userId == null) {
                    call.respond(HttpStatusCode.Unauthorized)
                    return@get
                }

                val lastSyncedAt = call.parameters["lastSyncedAt"]?.toLong() ?: 0

                val count = statisticsService.getUnsyncedDataCount(
                    userId = userId,
                    lastSyncedAt = lastSyncedAt
                )

                call.respond(
                    HttpStatusCode.OK,
                    RemoteUnsyncedDataCountResponse(
                        success = true,
                        message = "Unsynced data count retrieved successfully",
                        unsyncedLedgersCount = count.unsyncedLedgerCount,
                        unsyncedTransactionsCount = count.unsyncedTransactionCount
                    )
                )
            }
        }
    }
}