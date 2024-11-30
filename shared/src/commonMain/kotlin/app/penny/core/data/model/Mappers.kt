package app.penny.core.data.model


import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.Currency
import app.penny.core.domain.enum.LedgerCover
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.AchievementModel
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.UserAchievementModel
import app.penny.core.domain.model.UserModel
import app.penny.database.AchievementEntity
import app.penny.database.LedgerEntity
import app.penny.database.TransactionEntity
import app.penny.database.UserAchievementEntity
import app.penny.database.UserEntity
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.TransactionDto


@OptIn(ExperimentalUuidApi::class)
fun LedgerEntity.toLedgerModel(): LedgerModel {
    return LedgerModel(
        uuid = Uuid.parse(uuid),
        name = name,
        currency = Currency.valueOf(currency_code),
        cover = LedgerCover.valueOf(cover_name),
        description = description,
        count = 0,
        balance = BigDecimal.ZERO,
        createdAt = Instant.fromEpochSeconds(created_at),
        updatedAt = Instant.fromEpochSeconds(updated_at)
    )
}

@OptIn(ExperimentalUuidApi::class)
fun LedgerModel.toEntity(): LedgerEntity {
    return LedgerEntity(
        uuid = uuid.toString(),
        name = name,
        currency_code = currency.currencyCode,
        cover_name = cover.name,
        description = "",
        created_at = 0,
        updated_at = 0,
        user_uuid = userUuid.toString()
    )
}


@OptIn(ExperimentalUuidApi::class)
fun TransactionModel.toEntity(): TransactionEntity {
    return TransactionEntity(
        uuid = uuid.toString(),
        ledger_uuid = ledgerUuid.toString(),
        transaction_date = transactionDate.epochSeconds,
        category_name = category.name,
        transaction_type = transactionType.name,
        amount = amount.toPlainString(),
        currency_code = currency.currencyCode,
        remark = remark,
        screenshot_uri = screenshotUri,
        created_at = Clock.System.now().epochSeconds,
        updated_at = Clock.System.now().epochSeconds
    )
}

@OptIn(ExperimentalUuidApi::class)
fun TransactionEntity.toLedgerModel(): TransactionModel {
    return TransactionModel(
        ledgerUuid = Uuid.parse(ledger_uuid),
        transactionDate = Instant.fromEpochSeconds(transaction_date),
        category = Category.valueOf(category_name),
        transactionType = TransactionType.valueOf(transaction_type),
        amount = BigDecimal.parseString(amount),
        currency = Currency.valueOf(currency_code),
        screenshotUri = screenshot_uri,
        remark = remark,
    )
}


@OptIn(ExperimentalUuidApi::class)
fun TransactionModel.toTransactionDto(
    ledgerUuid: Uuid
): TransactionDto {
    return TransactionDto(
        userId = 0,
        uuid = uuid.toString(),
        ledgerUuid = ledgerUuid.toString(),
        transactionType = transactionType.name,
        transactionDate = transactionDate.epochSeconds,
        categoryName = category.name,
        currencyCode = currency.currencyCode,
        amount = amount.toPlainString(),
        remark = remark,
        createdAt = Clock.System.now().epochSeconds,
        updatedAt = Clock.System.now().epochSeconds,
    )
}


fun AchievementEntity.toLedgerModel(): AchievementModel {
    return AchievementModel(
        id = id,
        name = name,
        description = description,
        iconUri = icon_uri,
        goal = goal
    )
}




@OptIn(ExperimentalUuidApi::class)
fun LedgerModel.toLedgerDto(): LedgerDto {
    return LedgerDto(
        userId = 0,
        uuid = uuid.toString(),
        name = name,
        coverUri = "not implemented",
        currencyCode = currency.currencyCode,
        createdAt = createdAt.epochSeconds,
        updatedAt = updatedAt.epochSeconds

    )
}


@OptIn(ExperimentalUuidApi::class)
fun LedgerDto.toLedgerModel(): LedgerModel {
    return LedgerModel(
        uuid = Uuid.parse(uuid),
        name = name,
        currency = Currency.valueOf(currencyCode),
        description = "",
        count = 0,
        balance = BigDecimal.ZERO,
        createdAt = Instant.fromEpochSeconds(createdAt),
        updatedAt = Instant.fromEpochSeconds(updatedAt)
    )

}


@OptIn(ExperimentalUuidApi::class)
fun TransactionDto.toTransactionModel(): TransactionModel {
    return TransactionModel(
        transactionDate = Instant.fromEpochSeconds(transactionDate),
        category = Category.valueOf(categoryName),
        transactionType = TransactionType.valueOf(transactionType),
        amount = BigDecimal.parseString(amount),
        currency = Currency.valueOf(currencyCode),
        remark = remark,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}


@OptIn(ExperimentalUuidApi::class)
fun UserModel.toUserEntity(): UserEntity {
    return UserEntity(
        uuid = uuid.toString(),
        username = username,
        email = email,
        created_at = createdAt.epochSeconds,
        updated_at = updatedAt.epochSeconds
    )
}


@OptIn(ExperimentalUuidApi::class)
fun UserEntity.toUserModel(): UserModel {
    return UserModel(
        uuid = Uuid.parse(uuid),
        username = username,
        email = email,
        createdAt = Instant.fromEpochSeconds(created_at),
        updatedAt = Instant.fromEpochSeconds(updated_at)
    )
}
