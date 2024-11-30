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
    suspend fun upsert(ledgerModel: LedgerModel): Boolean

    suspend fun findByUserUuid(userUuid: Uuid): List<LedgerModel>




    suspend fun countByUserUuid(userUuid: Uuid): Long

    suspend fun countByUserUuidAndUpdatedAtAfter(
        userUuid: Uuid,
        timeStamp: Instant
    ): Long


    suspend fun findByUserUuidAndUpdatedAtAfter(
        userUuid: Uuid,
        timeStamp: Instant
    ): List<LedgerModel>


    //remoteDataSources
    suspend fun downloadUnsyncedLedgersByUserUuid(lastSyncedAt: Instant): List<LedgerModel>

    suspend fun uploadUnsyncedLedgersByUserUuid(
        ledgers: List<LedgerModel>,
        lastSyncedAt: Instant
    ): UploadLedgerResponse


}

