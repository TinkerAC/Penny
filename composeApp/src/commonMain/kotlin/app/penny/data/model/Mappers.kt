package app.penny.data.model


import app.penny.database.AchievementEntity
import app.penny.database.CategoryEntity
import app.penny.database.LedgerEntity
import app.penny.database.TransactionEntity
import app.penny.database.UserAchievementEntity
import app.penny.domain.enum.LedgerCover
import app.penny.domain.model.AchievementModel
import app.penny.domain.model.CategoryModel
import app.penny.domain.model.LedgerModel
import app.penny.domain.model.TransactionModel
import app.penny.domain.model.UserAchievementModel
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Clock

fun LedgerEntity.toModel(): LedgerModel {
    return LedgerModel(
        id = id,
        name = name,
        currencyCode = currency_code,
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
        currency_code = currencyCode,
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
        transaction_date = transactionDate,
        category_id = categoryId,
        transaction_type = transactionType.name,
        amount = amount.toString(),
        currency_code = currencyCode,
        content = content,
        screenshot_uri = screenshotUri,
        note = note,
        created_at = Clock.System.now().toEpochMilliseconds(),
        updated_at = Clock.System.now().toEpochMilliseconds()


    )
}


fun TransactionEntity.toModel(): TransactionModel {
    return TransactionModel(

        ledgerId = ledger_id,
        amount = BigDecimal.parseString(amount),
        currencyCode = currency_code,
        content = content,
        screenshotUri = screenshot_uri,
        note = note,

        )
}

fun CategoryEntity.toModel(): CategoryModel {
    return CategoryModel(
        id = id,
        parentId = parent_id,
        name = name,
        iconUri = icon_uri,

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




