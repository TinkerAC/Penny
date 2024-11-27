package app.penny.core.data.repository

import app.penny.core.domain.model.LedgerModel
import app.penny.servershared.dto.UploadLedgerResponse
import kotlinx.datetime.Instant

interface LedgerRepository {

    //localDataSources
    suspend fun addLedger(ledgerModel: LedgerModel)
    suspend fun findLedgerById(ledgerId: Long): LedgerModel
    suspend fun fetchAllLedgers(): List<LedgerModel>
    suspend fun updateLedger(ledgerModel: LedgerModel)
    suspend fun deleteLedger(ledgerId: Long)
    suspend fun findLedgersUpdatedAfter(timeStamp: Instant): List<LedgerModel>
    suspend fun upsertLedger(ledgerModel: LedgerModel)

    suspend fun countLedgersAfter(timeStamp: Instant): Int




    //remoteDataSources
    suspend fun downloadUnsyncedLedgers(lastSyncedAt: Instant): List<LedgerModel>

    suspend fun uploadUnsyncedLedgers(ledgers: List<LedgerModel>, lastSyncedAt: Instant): UploadLedgerResponse


}

