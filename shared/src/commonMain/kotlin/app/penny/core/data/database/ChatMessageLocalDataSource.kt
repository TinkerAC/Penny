package app.penny.core.data.database

import app.penny.database.ChatMessageEntity

interface ChatMessageLocalDataSource {

    fun insert(chatMessageEntity: ChatMessageEntity)
    fun upsertByUuid(chatMessageEntity: ChatMessageEntity)
    fun findByUuid(uuid: String): ChatMessageEntity?

    fun findAll(): List<ChatMessageEntity>

    fun findByUserUuid(userUuid: String): List<ChatMessageEntity>

    fun updateByUuid(chatMessageEntity: ChatMessageEntity)

    fun deleteByUuid(uuid: String)
}