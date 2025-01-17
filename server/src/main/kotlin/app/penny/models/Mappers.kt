// 文件：server/src/main/kotlin/app/penny/models/Extensions.kt
package app.penny.models

import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.TransactionDto
import app.penny.servershared.dto.UserDto
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toLedgerDto(
    userUuid: String
): LedgerDto {
    return LedgerDto(
        userUuid = userUuid,
        uuid = this[Ledgers.uuid],
        name = this[Ledgers.name],
        currencyCode = this[Ledgers.currencyCode],
        createdAt = this[Ledgers.createdAt],
        updatedAt = this[Ledgers.updatedAt]

    )
}

fun ResultRow.toTransactionDto(): TransactionDto {
    return TransactionDto(
        userId = this[Transactions.userId].value,
        uuid = this[Transactions.uuid],
        ledgerUuid = this[Transactions.ledgerUuid],
        transactionType = this[Transactions.transactionType],
        transactionDate = this[Transactions.transactionDate],
        currencyCode = this[Transactions.currencyCode],
        amount = this[Transactions.amount],
        remark = this[Transactions.remark],
        createdAt = this[Transactions.createdAt],
        updatedAt = this[Transactions.updatedAt],
        categoryName = this[Transactions.categoryName]
    )
}

fun ResultRow.toUserDto(): UserDto {
    return UserDto(
        id = this[Users.id].value,
        uuid = this[Users.uuid],
        username = this[Users.username],
        email = this[Users.email],
        createdAt = this[Users.createdAt],
        updatedAt = this[Users.updatedAt],
        passwordHash = this[Users.passwordHash]
    )
}