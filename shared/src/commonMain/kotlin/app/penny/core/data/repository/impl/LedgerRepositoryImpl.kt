package app.penny.core.data.repository.impl

import app.penny.core.data.database.LedgerLocalDataSource
import app.penny.core.data.model.toEntity
import app.penny.core.data.model.toLedgerDto
import app.penny.core.data.model.toModel
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.domain.model.LedgerModel
import app.penny.core.network.ApiClient
import app.penny.servershared.dto.responseDto.UploadLedgerResponse
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)

class LedgerRepositoryImpl(
    private val apiClient: ApiClient,
    private val ledgerLocalDataSource: LedgerLocalDataSource
) : LedgerRepository {

    override suspend fun insert(ledgerModel: LedgerModel) {
        ledgerLocalDataSource.insert(ledgerModel.toEntity())
    }

    override suspend fun findByUuid(ledgerUuid: Uuid): LedgerModel? {
        return ledgerLocalDataSource.findByUuid(
            ledgerUuid.toString()
        )?.toModel()
    }

    override suspend fun findAll(): List<LedgerModel> {
        return ledgerLocalDataSource.findAll().map { it.toModel() }
    }

    override suspend fun update(ledgerModel: LedgerModel) {
        ledgerLocalDataSource.updateByUuid(ledgerModel.toEntity())
    }


    override suspend fun deleteByUuid(ledgerUuid: Uuid) {
        ledgerLocalDataSource.deleteByUuid(ledgerUuid.toString())
    }



    override suspend fun downloadUnsyncedLedgersByUserUuid(lastSyncedAt: Instant): List<LedgerModel> {
        val downloadLedgerResponse = apiClient.sync.downloadLedgers(lastSyncedAt.epochSeconds)

        return downloadLedgerResponse.ledgers.map { it.toModel() }
    }

    override suspend fun uploadUnsyncedLedgersByUserUuid(
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
        val existingLedger = ledgerLocalDataSource.findByUuid(ledgerModel.uuid.toString())

        if (existingLedger != null) {
            // 执行更新
            ledgerLocalDataSource.updateByUuid(ledgerModel.toEntity())
        } else {
            insertedNewLedger = true
            // 执行插入
            ledgerLocalDataSource.insert(ledgerModel.toEntity())
        }
        return insertedNewLedger
    }


    override suspend fun countByUserUuidAndUpdatedAtAfter(
        userUuid: Uuid,
        timeStamp: Instant
    ): Long {
        return ledgerLocalDataSource.countByUserUuidAndUpdatedAtAfter(
            userUuid.toString(),
            timeStamp.epochSeconds
        )
    }

    override suspend fun findByUserUuid(userUuid: Uuid): List<LedgerModel> {
        return ledgerLocalDataSource.findByUserUuid(userUuid.toString()).map { it.toModel() }
    }

    override suspend fun countByUserUuid(userUuid: Uuid): Long {
        return ledgerLocalDataSource.countByUserUuid(userUuid.toString())
    }

    override suspend fun findByUserUuidAndUpdatedAtAfter(
        userUuid: Uuid,
        timeStamp: Instant
    ): List<LedgerModel> {
        return ledgerLocalDataSource.findByUserUuidAndUpdatedAtAfter(
            userUuid.toString(),
            timeStamp.epochSeconds
        ).map { it.toModel() }
    }
}