package app.penny.data.model


import app.penny.database.TransactionEntity
import app.penny.domain.model.Transaction
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

// TransactionEntity 转换为 Transaction（Domain 模型）
fun TransactionEntity.toDomainModel(): Transaction {
    return Transaction(
        id = this.transaction_id.toString(),
        amount = this.amount.toInt(),
        // 从Unix时间戳转换为LocalDateTime
        date = Instant.fromEpochMilliseconds(this.transaction_date)
            .toLocalDateTime(TimeZone.UTC) // 或根据需要选择其他时区
    )
}


// Model 转换为 Entity
//fun TransactionEntity.toD


//// TransactionDto 转换为 Transaction（Domain 模型）
//fun TransactionDto.toDomainModel(): Transaction {
//    return Transaction(
//        id = this.id,
//        amount = this.amount,
//        date = this.timestamp.toLocalDateTime()
//    )
//}
//
//// TransactionDto 转换为 TransactionEntity
//fun TransactionDto.toEntity(): TransactionEntity {
//    return TransactionEntity(
//        id = this.id,
//        amount = this.amount,
//        timestamp = this.timestamp
//    )
//}
