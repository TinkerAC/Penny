package app.penny.core.data.repository.impl

import app.penny.core.data.database.LedgerLocalDataSource
import app.penny.core.data.model.toEntity
import app.penny.core.data.model.toLedgerDto
import app.penny.core.data.model.toLedgerModel
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.domain.model.LedgerModel
import app.penny.core.network.ApiClient
import app.penny.servershared.dto.UploadLedgerResponse
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)

class LedgerRepositoryImpl(
    private val apiClient: ApiClient,
    private val ledgerLocalDataSource: LedgerLocalDataSource
) : LedgerRepository {

    override suspend fun addLedger(ledgerModel: LedgerModel) {
        ledgerModel.uuid = Uuid.random()

        val ledgerEntity = ledgerModel.toEntity()

        ledgerLocalDataSource.insertLedger(ledgerEntity)
    }

    override suspend fun findLedgerById(ledgerId: Long): LedgerModel {
        return ledgerLocalDataSource.getLedgerById(ledgerId).toLedgerModel()
    }

    override suspend fun fetchAllLedgers(): List<LedgerModel> {
        return ledgerLocalDataSource.getAllLedgers().map { it.toLedgerModel() }
    }

    override suspend fun updateLedger(ledgerModel: LedgerModel) {
        ledgerLocalDataSource.updateLedger(ledgerModel.toEntity())
    }


    override suspend fun deleteLedger(ledgerId: Long) {
        ledgerLocalDataSource.deleteLedger(ledgerId)
    }

    override suspend fun findLedgersUpdatedAfter(timeStamp: Instant): List<LedgerModel> {
        return ledgerLocalDataSource.getLedgersUpdatedAfter(
            timeStamp.epochSeconds
        ).map {
            it.toLedgerModel()
        }
    }

    override suspend fun downloadUnsyncedLedgers(lastSyncedAt: Instant): List<LedgerModel> {
        val downloadLedgerResponse = apiClient.sync.downloadLedgers(lastSyncedAt.epochSeconds)

        return downloadLedgerResponse.ledgers.map { it.toLedgerModel() }
    }

    override suspend fun uploadUnsyncedLedgers(
        ledgers: List<LedgerModel>,
        lastSyncedAt: Instant
    ): UploadLedgerResponse {
        val ledgerDtos = ledgers.map { it.toLedgerDto() }
        val uploadUpdatedLedgersResponse =
            apiClient.sync.uploadLedgers(ledgerDtos, lastSyncedAt.epochSeconds)

        return uploadUpdatedLedgersResponse
    }


    override suspend fun upsertLedger(ledgerModel: LedgerModel) {
        ledgerLocalDataSource.upsertLedgerByUuid(ledgerModel.toEntity())
    }


    override suspend fun countLedgersAfter(timeStamp: Instant): Int {
        return ledgerLocalDataSource.countLedgersAfter(timeStamp.epochSeconds)
    }




}