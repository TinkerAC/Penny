package app.penny.core.data.repository

import app.penny.core.domain.model.LedgerModel
import app.penny.servershared.dto.UploadLedgerResponse
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
interface LedgerRepository {

    //localDataSources
    suspend fun insert(ledgerModel: LedgerModel)
    suspend fun findByUuid(ledgerUuid: Uuid): LedgerModel?
    suspend fun findAll(): List<LedgerModel>
    suspend fun update(ledgerModel: LedgerModel)
    suspend fun deleteByUuid(ledgerUuid: Uuid)
    suspend fun findByUpdatedAtAfter(timeStamp: Instant): List<LedgerModel>
    suspend fun upsert(ledgerModel: LedgerModel): Boolean

    suspend fun countByUpdatedAtAfter(timeStamp: Instant): Int

    //remoteDataSources
    suspend fun downloadUnsyncedLedgers(lastSyncedAt: Instant): List<LedgerModel>

    suspend fun uploadUnsyncedLedgers(
        ledgers: List<LedgerModel>,
        lastSyncedAt: Instant
    ): UploadLedgerResponse


}

