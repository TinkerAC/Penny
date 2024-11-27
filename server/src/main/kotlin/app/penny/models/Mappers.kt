package app.penny.models

import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.TransactionDto
import app.penny.servershared.dto.UserDto
import org.jetbrains.exposed.sql.ResultRow
import kotlin.concurrent.thread


fun ResultRow.toUserDto(): UserDto {
    return UserDto(
        id = this[Users.id].value.toLong(),
        email = this[Users.email],
        username = this[Users.username],
        createdAt = this[Users.createdAt],
    )
}


fun ResultRow.toLedgerDto(): LedgerDto {
    return LedgerDto(
        uuid = this[Ledgers.uuid],
        name = this[Ledgers.name],
        currencyCode = this[Ledgers.currencyCode],
        createdAt = this[Ledgers.createdAt],
        updatedAt = this[Ledgers.updatedAt],
    )
}


fun ResultRow.toTransactionDto(): TransactionDto {
    return TransactionDto(
        uuid = this[Transactions.uuid],
        ledgerUuid = this[Transactions.ledgerUuid],
        amount = this[Transactions.amount],
        currencyCode = this[Transactions.currencyCode],
        createdAt = this[Transactions.createdAt],
        updatedAt = this[Transactions.updatedAt],
        transactionDate = this[Transactions.transactionDate],
        transactionType = this[Transactions.transactionType],
        category = this[Transactions.categoryName],
    )
}


