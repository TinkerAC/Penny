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

    override suspend fun insert(ledgerModel: LedgerModel) {
        ledgerModel.uuid = Uuid.random()

        val ledgerEntity = ledgerModel.toEntity()

        ledgerLocalDataSource.insertLedger(ledgerEntity)
    }

    override suspend fun findByUuid(ledgerUuid: Uuid): LedgerModel? {
        return ledgerLocalDataSource.getLedgerByUuid(
            ledgerUuid.toString()
        )?.toLedgerModel()
    }

    override suspend fun findAll(): List<LedgerModel> {
        return ledgerLocalDataSource.getAllLedgers().map { it.toLedgerModel() }
    }

    override suspend fun update(ledgerModel: LedgerModel) {
        ledgerLocalDataSource.updateLedger(ledgerModel.toEntity())
    }


    override suspend fun deleteByUuid(ledgerUuid: Uuid) {
        ledgerLocalDataSource.deleteLedger(ledgerUuid.toString())
    }

    override suspend fun findByUpdatedAtAfter(timeStamp: Instant): List<LedgerModel> {
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


    override suspend fun upsert(ledgerModel: LedgerModel): Boolean {
        var insertedNewLedger: Boolean = false
        // 尝试查询是否存在目标记录
        val existingLedger = ledgerLocalDataSource.getLedgerByUuid(ledgerModel.uuid.toString())

        if (existingLedger != null) {
            // 执行更新
            ledgerLocalDataSource.updateLedger(ledgerModel.toEntity())
        } else {
            insertedNewLedger = true
            // 执行插入
            ledgerLocalDataSource.insertLedger(ledgerModel.toEntity())
        }
        return insertedNewLedger
    }


    override suspend fun countByUpdatedAtAfter(timeStamp: Instant): Int {
        return ledgerLocalDataSource.countLedgersAfter(timeStamp.epochSeconds)
    }


}