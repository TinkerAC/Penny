// 文件：server/src/main/kotlin/app/penny/repository/TransactionRepository.kt
package app.penny.repository

import app.penny.servershared.dto.TransactionDto

interface TransactionRepository {
    fun findByUserIdAndUpdatedAfter(userId: Long, timeStamp: Long): List<TransactionDto>
    fun findByUuid(uuid: String): TransactionDto?
    fun insert(transactions: List<TransactionDto>)
    fun countByUserIdUpdatedAfter(userId: Long, timeStamp: Long): Long
    fun upsert(transaction: TransactionDto)
}