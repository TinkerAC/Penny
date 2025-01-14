package app.penny.core.data.enumerate

/**
 * This file contains extension functions for converting between various data models,
 * including database entities, domain models, and DTOs .
 *
 * It includes conversions for:
 * - Entity to model (e.g., `LedgerEntity` to `LedgerModel`).
 * - Model to DTO (e.g., `TransactionModel` to `TransactionDto`).
 * - DTO to model (e.g., `TransactionDto` to `TransactionModel`).
 * - Conversions between different types of `ChatMessage` (user/system messages).
 */


import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.enumerate.Category
import app.penny.core.domain.enumerate.Currency
import app.penny.core.domain.enumerate.LedgerCover
import app.penny.core.domain.enumerate.TransactionType
import app.penny.core.domain.model.AchievementModel
import app.penny.core.domain.model.ChatMessage
import app.penny.core.domain.model.LedgerModel
import app.penny.core.domain.model.SystemMessage
import app.penny.core.domain.model.TransactionModel
import app.penny.core.domain.model.UserMessage
import app.penny.core.domain.model.UserModel
import app.penny.database.ChatMessageEntity
import app.penny.database.LedgerEntity
import app.penny.database.TransactionEntity
import app.penny.database.UserEntity
import app.penny.di.getKoinInstance
import app.penny.servershared.dto.LedgerDto
import app.penny.servershared.dto.TransactionDto
import app.penny.servershared.dto.UserDto
import app.penny.servershared.enumerate.UserIntent
import com.ionspin.kotlin.bignum.decimal.BigDecimal
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


val json = Json {
    ignoreUnknownKeys = true
    encodeDefaults = true
}

@OptIn(ExperimentalUuidApi::class)
fun LedgerEntity.toModel(): LedgerModel {
    return LedgerModel(
        userUuid = Uuid.parse(user_uuid),
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
        currency_code = currency.code,
        cover_name = cover.name,
        description = "",
        created_at = 0,
        updated_at = 0,
        user_uuid = userUuid.toString(),
        budget_amount = BigDecimal.ZERO.toPlainString()
    )
}


@OptIn(ExperimentalUuidApi::class)
fun TransactionModel.toEntity(): TransactionEntity {
    return TransactionEntity(
        uuid = uuid.toString(),
        ledger_uuid = ledgerUuid.toString(),
        transaction_date = transactionInstant.epochSeconds,
        category_name = category.name,
        transaction_type = transactionType.name,
        amount = amount.toPlainString(),
        currency_code = currency.code,
        remark = remark,
        screenshot_uri = screenshotUri,
        created_at = Clock.System.now().epochSeconds,
        updated_at = Clock.System.now().epochSeconds
    )
}

@OptIn(ExperimentalUuidApi::class)
fun TransactionEntity.toModel(): TransactionModel {
    return TransactionModel(
        uuid = Uuid.parse(uuid),
        ledgerUuid = Uuid.parse(ledger_uuid),
        transactionInstant = Instant.fromEpochSeconds(transaction_date),
        category = Category.valueOf(category_name),
        transactionType = TransactionType.valueOf(transaction_type),
        amount = BigDecimal.parseString(amount),
        currency = Currency.valueOf(currency_code),
        screenshotUri = screenshot_uri,
        remark = remark,
    )
}


@OptIn(ExperimentalUuidApi::class)
fun TransactionModel.toDto(
    ledgerUuid: Uuid
): TransactionDto {
    return TransactionDto(
        userId = 0,
        uuid = uuid.toString(),
        ledgerUuid = ledgerUuid.toString(),
        transactionType = transactionType.name,
        transactionDate = transactionInstant.epochSeconds,
        categoryName = category.name,
        currencyCode = currency.code,
        amount = amount.toPlainString(),
        remark = remark,
        createdAt = Clock.System.now().epochSeconds,
        updatedAt = Clock.System.now().epochSeconds,
    )
}





@OptIn(ExperimentalUuidApi::class)
fun LedgerModel.toLedgerDto(): LedgerDto {
    return LedgerDto(
        userUuid = userUuid.toString(),
        uuid = uuid.toString(),
        name = name,
        coverUri = "not implemented",
        currencyCode = currency.code,
        createdAt = createdAt.epochSeconds,
        updatedAt = updatedAt.epochSeconds

    )
}


@OptIn(ExperimentalUuidApi::class)
fun LedgerDto.toModel(): LedgerModel {
    return LedgerModel(
        userUuid = Uuid.parse(userUuid),
        uuid = Uuid.parse(uuid),
        name = name,
        currency = currencyCode.let { Currency.valueOf(it) },
        description = "",
        count = 0,
        balance = BigDecimal.ZERO,
        createdAt = Instant.fromEpochSeconds(createdAt),
        updatedAt = Instant.fromEpochSeconds(updatedAt)
    )

}


@OptIn(ExperimentalUuidApi::class)
fun TransactionDto.toModel(): TransactionModel {
    return TransactionModel(
        uuid = Uuid.parse(uuid),
        ledgerUuid = Uuid.parse(ledgerUuid),
        transactionInstant = Instant.fromEpochSeconds(transactionDate),
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
fun UserModel.toEntity(): UserEntity {
    return UserEntity(
        uuid = uuid.toString(),
        username = username,
        email = email,
        created_at = createdAt.epochSeconds,
        updated_at = updatedAt.epochSeconds
    )
}


@OptIn(ExperimentalUuidApi::class)
fun UserEntity.toModel(): UserModel {
    return UserModel(
        uuid = Uuid.parse(uuid),
        username = username,
        email = email,
        createdAt = Instant.fromEpochSeconds(created_at),
        updatedAt = Instant.fromEpochSeconds(updated_at)
    )
}


@OptIn(ExperimentalUuidApi::class)
fun ChatMessage.toEntity(): ChatMessageEntity {
    when (this) {
        is UserMessage -> {
            return ChatMessageEntity(
                uuid = uuid.toString(),
                type = type.name,
                user_uuid = user.uuid.toString(),
                sender_uuid = user.uuid.toString(),
                content = content,
                timestamp = timestamp,
//                audio_file_path = audioFilePath, //TODO: implement audio file path
//                duration = duration,
                user_intent = null,
                last_log = null,
                duration = null,

                )
        }

        is SystemMessage -> {
            return ChatMessageEntity(
                uuid = uuid.toString(),
                user_uuid = user.uuid.toString(),
                sender_uuid = sender.uuid.toString(),
                content = content,
                timestamp = timestamp,
//                audio_file_path = null,
                duration = null,
                type = type.name,
                user_intent = userIntent?.let { json.encodeToString(it) },
                last_log = executeLog

            )
        }
    }


}

val userRepository = getKoinInstance<UserRepository>()

@OptIn(ExperimentalUuidApi::class)
fun ChatMessageEntity.toModel(): ChatMessage {
    return when (sender_uuid) {
        UserModel.System.uuid.toString() -> {
            SystemMessage(
                user = UserModel(Uuid.parse(user_uuid), "", ""),
                type = MessageType.valueOf(type),
                uuid = Uuid.parse(uuid),
                timestamp = timestamp,
                sender = UserModel.System,
                userIntent =
                user_intent?.let {
                    json.decodeFromString(
                        UserIntent.serializer(),
                        it
                    )
                } ?: UserIntent.JustTalk(),
                content = content

            )
        }

        else -> {
            UserMessage(
                uuid = Uuid.parse(uuid),
                user = UserModel(Uuid.parse(user_uuid), "", ""),
                sender = UserModel(Uuid.parse(sender_uuid), "", ""),
                type = MessageType.valueOf(type),
                timestamp = timestamp,
                content = content,
                duration = null
            )
        }

    }
}


@OptIn(ExperimentalUuidApi::class)
fun UserDto.toUserModel(): UserModel {

    return UserModel(
        uuid = Uuid.parse(uuid),
        username = username ?: "",
        email = email,
        createdAt = Instant.fromEpochSeconds(createdAt),
        updatedAt = Instant.fromEpochSeconds(updatedAt)
    )
}