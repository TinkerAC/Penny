package app.penny.servershared.dto

import kotlinx.serialization.Serializable

@Serializable
data class RemoteUnsyncedDataCount(
    override val success: Boolean,
    override val message: String,
    val unsyncedLedgersCount: Int,
    val unsyncedTransactionsCount: Int
) : BaseResponseDto()
