package app.penny.routes

import io.ktor.server.response.*
import io.ktor.server.request.*
import io.ktor.http.*
import io.ktor.server.routing.*

fun Route.syncRoutes() {
    route("/sync") {
        get("/ping") {
            call.respondText("Pong")
        }


        get("/pull") {
            val lastSyncTime = call.request.queryParameters["last_sync_time"]?.toLongOrNull() ?: 0
            val updatedRecords = transaction {
                EntityTable.select { EntityTable.updatedAt greater lastSyncTime }
                    .map { toEntityDTO(it) }
            }
            call.respond(updatedRecords)
        }

        post("/upload") {
            call.respondText("Upload")
        }


    }


}