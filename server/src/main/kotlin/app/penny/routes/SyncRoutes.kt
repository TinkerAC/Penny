package app.penny.routes

// Ktor相关

// Exposed相关
import app.penny.config.JwtConfig
import app.penny.servershared.dto.PushLedgersRequest
import app.penny.servershared.dto.PushLedgersResponse
import app.penny.servershared.dto.RegisterResponse
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

            get("/pull") {


                val jwt = call.request.headers["Authorization"]

                if (jwt == null) {
                    call.respond(
                        HttpStatusCode.Unauthorized,
                        RegisterResponse(
                            success = false,
                            message = "No token provided"
                        )
                    )
                    return@get
                }

                val userId = jwtConfig.getUserIdFromToken(jwt)



//                call.respond(
////                    ledgers
//                )

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

                val pushLedgerRequest: PushLedgersRequest = call.receive()

                val ledgerDTOs = pushLedgerRequest.ledgers

                try {
                    ledgerService.insertLedgers(
                        ledgerDTOs
                    )
                } catch (e: Exception) {
                    call.respond(
                        PushLedgersResponse(
                            success = true,
                            changedLines = 0
                        )
                    )
                }

                call.respond(
                    PushLedgersResponse(
                        success = true,
                        changedLines = ledgerDTOs.size
                    )
                )


            }
        }

        route("/transaction") {

        }


    }


}