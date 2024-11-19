package app.penny.data.model


import app.penny.database.AchievementEntity
import app.penny.database.LedgerEntity
import app.penny.database.TransactionEntity
import app.penny.database.UserAchievementEntity
import app.penny.domain.enum.Category
import app.penny.domain.enum.Currency
import app.penny.domain.enum.LedgerCover
import app.penny.domain.enum.TransactionType
import app.penny.domain.model.AchievementModel
import app.penny.domain.model.LedgerModel
import app.penny.domain.model.TransactionModel
import app.penny.domain.model.UserAchievementModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant


fun LedgerEntity.toModel(): LedgerModel {
    return LedgerModel(
        id = id,
        name = name,
        currency = Currency.valueOf(currency_code),
        cover = LedgerCover.valueOf(cover_name),
        description = description,
        count = 0,
        balance = BigDecimal.ZERO

    )
}

fun LedgerModel.toEntity(): LedgerEntity {
    return LedgerEntity(
        id = id,
        name = name,
        currency_code = currency.currencyCode,
        cover_name = cover.name,
        description = "",
        created_at = 0,
        updated_at = 0
    )
}


fun TransactionModel.toEntity(): TransactionEntity {
    return TransactionEntity(
        id = 0,
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


fun TransactionEntity.toModel(): TransactionModel {
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


fun AchievementEntity.toModel(): AchievementModel {
    return AchievementModel(
        id = id,
        name = name,
        description = description,
        iconUri = icon_uri,
        goal = goal
    )
}

fun UserAchievementEntity.toModel(): UserAchievementModel {
    return UserAchievementModel(
        id = id,
        achievementId = achievement_id,
        progress = progress,
        completedAt = completed_at,

        )
}




