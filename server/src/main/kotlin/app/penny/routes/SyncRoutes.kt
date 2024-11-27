// file: server/src/main/kotlin/app/penny/routes/SyncRoutes.kt
package app.penny.routes

import app.penny.servershared.dto.DownloadLedgerRequest
import app.penny.servershared.dto.DownloadLedgerResponse
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.UploadLedgerRequest
import app.penny.servershared.dto.UploadLedgerResponse
import app.penny.services.LedgerService
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.authenticate
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import kotlinx.datetime.Clock

fun Route.syncRoutes(
    ledgerService: LedgerService,
) {
    route("/sync") {
        authenticate("access-jwt") {
            route("/ledger") {
                get("/download") {
                    val principal = call.principal<JWTPrincipal>()

                    val userId = principal?.getClaim("userId", String::class)

                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@get
                    }

                    val lastSyncedAt = call.parameters["lastSyncedAt"]?.toLong() ?: 0

                    val ledgers: List<LedgerDto> = ledgerService.getLedgersByUserIdAfterLastSync(
                        userId.toInt(),
                        lastSyncedAt
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
                    val principal = call.principal<JWTPrincipal>()
                    val userId = principal!!.getClaim("userId", String::class)

                    if (userId == null) {
                        call.respond(HttpStatusCode.Unauthorized)
                        return@post
                    }

                    val uploadRequest: UploadLedgerRequest = call.receive()
                    val ledgerDTOs = uploadRequest.ledgers

                    try {
                        ledgerService.insertLedgers(
                            ledgerDTOs,
                            userId.toInt()
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
                            message = "Ledgers uploaded successfully",
                            changedLines = ledgerDTOs.size,
                            lastSyncedAt = Clock.System.now().epochSeconds
                        )
                    )
                }
            }
        }
    }
}
