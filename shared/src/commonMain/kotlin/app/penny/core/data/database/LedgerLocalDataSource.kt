package app.penny.core.data.database

import app.penny.database.LedgerEntity

interface LedgerLocalDataSource {


    fun insert(ledgerEntity: LedgerEntity)

    fun upsertByUuid(ledgerEntity: LedgerEntity)

    fun findByUuid(uuid: String): LedgerEntity?

    fun findAll(): List<LedgerEntity>

    fun findByUserUuid(userUuid: String): List<LedgerEntity>

    fun updateByUuid(ledgerEntity: LedgerEntity)

    fun deleteByUuid(uuid: String)


    fun countByUserUuid(userUuid: String): Long


    fun findByUserUuidAndUpdatedAtAfter(userUuid: String, timestamp: Long): List<LedgerEntity>
    fun countByUserUuidAndUpdatedAtAfter(toString: String, epochSeconds: Long): Long

}