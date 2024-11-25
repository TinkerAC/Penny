// file: server/src/main/kotlin/app/penny/routes/SyncRoutes.kt
package app.penny.routes

import app.penny.config.JwtConfig
import app.penny.servershared.dto.DownloadLedgerRequest
import app.penny.servershared.dto.DownloadLedgerResponse
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.UploadLedgerRequest
import app.penny.servershared.dto.UploadLedgerResponse
import app.penny.services.LedgerService
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.call
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import kotlinx.datetime.Clock

fun Route.syncRoutes(
    ledgerService: LedgerService,
    jwtConfig: JwtConfig
) {
    route("/sync") {
        route("/ledger") {
            post("/download") {
                val jwt = call.request.headers["Authorization"]
                if (jwt == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        DownloadLedgerResponse(
                            total = 0,
                            ledgers = emptyList(),
                            lastSyncedAt = 0,
                            message = "Unauthorized"
                        )
                    )
                    return@post
                }

                val userId = jwtConfig.getUserIdFromToken(jwt)
                val downloadRequest = call.receive<DownloadLedgerRequest>()
                val lastSyncedAt = downloadRequest.lastSyncedAt

                val ledgers: List<LedgerDto> = ledgerService.getLedgersByUserIdAfterLastSync(
                    userId,
                    lastSyncedAt
                )

                call.respond(
                    DownloadLedgerResponse(
                        total = ledgers.size,
                        ledgers = ledgers,
                        lastSyncedAt = Clock.System.now().epochSeconds
                    )
                )
            }

            post("/upload") {
                val jwt = call.request.headers["Authorization"]
                if (jwt == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        UploadLedgerResponse(
                            success = false,
                            changedLines = 0,
                            lastSyncedAt = Clock.System.now().epochSeconds,
                            message = "No access token provided"
                        )
                    )
                    return@post
                }

                val userId = jwtConfig.getUserIdFromToken(jwt)
                val uploadRequest: UploadLedgerRequest = call.receive()
                val ledgerDTOs = uploadRequest.ledgers

                try {
                    ledgerService.insertLedgers(
                        ledgerDTOs,
                        userId
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
                    return@post
                }

                call.respond(
                    UploadLedgerResponse(
                        success = true,
                        changedLines = ledgerDTOs.size,
                        lastSyncedAt = Clock.System.now().epochSeconds
                    )
                )
            }
        }
    }
}
