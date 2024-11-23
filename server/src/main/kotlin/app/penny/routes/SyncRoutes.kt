package app.penny.routes

// Ktor相关

// Exposed相关
import app.penny.config.JwtConfig
import app.penny.servershared.dto.DownloadLedgerResponse
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.RegisterResponse
import app.penny.servershared.dto.UploadLedgerRequest
import app.penny.servershared.dto.UploadLedgerResponse
import app.penny.services.LedgerService
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route

// 项目相关

fun Route.syncRoutes(
    ledgerService: LedgerService,
    jwtConfig: JwtConfig
) {
    route("/sync") {

        route(
            "/ledger",
        ) {

            get("/download") {


                val jwt = call.request.headers["Authorization"]

                if (jwt == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        DownloadLedgerResponse(
                            total = 0,
                            ledgers = emptyList(),
                            lastSyncedAt = 0
                        )
                    )
                    return@get
                }

                val userId = jwtConfig.getUserIdFromToken(jwt)


                val ledgerDownloadRequest = call.receive<UploadLedgerRequest>()

                val lastSyncedAt = ledgerDownloadRequest.lastSyncedAt

                val ledgers: List<LedgerDto> = ledgerService.getLedgersByUserIdAfterLastSync(
                    userId,
                    lastSyncedAt
                )

                call.respond(
                    DownloadLedgerResponse(
                        total = ledgers.size,
                        ledgers = ledgers,
                        lastSyncedAt = System.currentTimeMillis()
                    )
                )


            }

            post("/upload") {

                if (call.request.headers["Authorization"] == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        RegisterResponse(
                            success = false,
                            message = "No token provided"
                        )
                    )
                }

                val pushLedgerRequest: UploadLedgerRequest = call.receive()

                val ledgerDTOs = pushLedgerRequest.ledgers

                try {
                    ledgerService.insertLedgers(
                        ledgerDTOs
                    )
                } catch (e: Exception) {
                    call.respond(
                        UploadLedgerResponse(
                            success = true,
                            changedLines = 0
                        )
                    )
                }

                call.respond(
                    UploadLedgerResponse(
                        success = true,
                        changedLines = ledgerDTOs.size
                    )
                )
                
            }
        }


    }


}