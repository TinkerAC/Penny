package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class PushLedgersRequest(

    val total: Int,
    val ledgers: List<LedgerDto>
)