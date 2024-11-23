package app.penny.routes

// Ktor相关
import app.penny.models.Ledgers
import app.penny.servershared.dto.PushLedgersRequest
import app.penny.services.LedgerService
import io.ktor.server.application.*
import io.ktor.server.routing.*  // 用于定义路由
import io.ktor.server.request.*  // 用于处理请求
import io.ktor.server.response.*  // 用于发送响应
import io.ktor.http.*  // HTTP相关

// Exposed相关
import org.jetbrains.exposed.sql.*  // 用于操作数据库
import org.jetbrains.exposed.sql.transactions.transaction  // 用于数据库事务

// 项目相关

fun Route.syncRoutes(
    ledgerService: LedgerService
) {
    route("/sync") {

        route(
            "/ledger",
        ) {

            get("/pull") {

            }

            post("/push") {

                val pushLedgerRequest: PushLedgersRequest = call.receive()

                val ledgerDTOs = pushLedgerRequest.ledgers

                ledgerService.insertLedgers(
                    ledgerDTOs
                )



            }
        }

        route("/transaction") {

        }


    }


}