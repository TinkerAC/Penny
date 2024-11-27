package app.penny.core.data.model


import app.penny.core.domain.enum.Category
import app.penny.core.domain.enum.Currency
import app.penny.core.domain.enum.LedgerCover
import app.penny.core.domain.enum.TransactionType
import app.penny.core.domain.model.AchievementModel
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.UserAchievementModel
import app.penny.database.AchievementEntity
import app.penny.database.LedgerEntity
import app.penny.database.TransactionEntity
import app.penny.database.UserAchievementEntity
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
        id = id,
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
        id = id,
        uuid = uuid.toString(),
        name = name,
        currency_code = currency.currencyCode,
        cover_name = cover.name,
        description = "",
        created_at = 0,
        updated_at = 0
    )
}


@OptIn(ExperimentalUuidApi::class)
fun TransactionModel.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = 0,
        uuid = Uuid.toString(),
        ledger_id = ledgerId,
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
        ledgerId = ledger_id,
        amount = BigDecimal.parseString(amount),
        currency = Currency.valueOf(currency_code),
        remark = remark,
        transactionType = TransactionType.valueOf(transaction_type),
        screenshotUri = screenshot_uri,
        transactionDate = Instant.fromEpochSeconds(transaction_date),
        category = Category.valueOf(category_name),
    )
}


@OptIn(ExperimentalUuidApi::class)
fun TransactionModel.toTransactionDto(
    ledgerUuid: Uuid
): TransactionDto {
    return TransactionDto(
        uuid = uuid.toString(),
        ledgerUuid = ledgerUuid.toString(),
        transactionDate = transactionDate.epochSeconds,
        category = category.name,
        transactionType = transactionType.name,
        amount = amount.toPlainString(),
        currencyCode = currency.currencyCode,
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

fun UserAchievementEntity.toLedgerModel(): UserAchievementModel {
    return UserAchievementModel(
        id = id,
        achievementId = achievement_id,
        progress = progress,
        completedAt = completed_at,

        )
}


@OptIn(ExperimentalUuidApi::class)
fun LedgerModel.toLedgerDto(): LedgerDto {
    return LedgerDto(
        uuid = uuid.toString(),
        name = name,
        currencyCode = currency.currencyCode,
        updatedAt = updatedAt.epochSeconds,
        createdAt = createdAt.epochSeconds,
        coverUri = "not implemented"
    )
}


@OptIn(ExperimentalUuidApi::class)
fun LedgerDto.toLedgerModel(): LedgerModel {
    return LedgerModel(
        id = 0,
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
        ledgerId = 0,
        transactionDate = Instant.fromEpochSeconds(transactionDate),
        category = Category.valueOf(category),
        transactionType = TransactionType.valueOf(transactionType),
        amount = BigDecimal.parseString(amount),
        currency = Currency.valueOf(currencyCode),
        remark = remark,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}
