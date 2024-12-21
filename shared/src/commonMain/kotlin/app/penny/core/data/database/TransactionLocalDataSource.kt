package app.penny.core.data.database

import app.penny.database.TransactionEntity

interface TransactionLocalDataSource {


    fun insert(transaction: TransactionEntity)

    fun upsertByUuid(transaction: TransactionEntity)

    fun findByUuid(uuid: String): TransactionEntity?


    fun findByUserUuid(userUuid: String): List<TransactionEntity>


    fun findAll(): List<TransactionEntity>

    fun findByLedgerUuid(ledgerUuid: String): List<TransactionEntity>


    fun findByLedgerUuidAndTransactionDateBetween(
        ledgerUuid: String,
        startTimestamp: Long,
        endTimestamp: Long
    ): List<TransactionEntity>

    fun updateByUuid(transaction: TransactionEntity)

    fun deleteByUuid(uuid: String)

    fun count(): Long

    fun countByUserUuidAndUpdatedAtAfter(
        userUuid: String,
        timestamp: Long
    ): Long


    fun findByUserUuidAndUpdatedAtAfter(
        userUuid: String,
        timestamp: Long
    ): List<TransactionEntity>

    fun countByLedgerUuid(ledgerUuid: String): Long

    fun findByUserUuidAndTransactionDateBetween(
        userUuid: String,
        startEpochSeconds: Long,
        endEpochSeconds: Long
    ): List<TransactionEntity>


    fun findRecentByLedgerUuid(
        ledgerUuid: String, limit: Long
    ): List<TransactionEntity>

    fun findByLedgerUuids(ledgerUuids: List<String>): List<TransactionEntity>







}