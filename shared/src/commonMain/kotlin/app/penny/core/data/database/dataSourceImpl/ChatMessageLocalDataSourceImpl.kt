package app.penny.core.data.database.dataSourceImpl

import app.penny.core.data.database.ChatMessageLocalDataSource
import app.penny.database.ChatMessageEntity
import app.penny.database.ChatMessageQueries

class ChatMessageLocalDataSourceImpl(
    private val chatMessageQueries: ChatMessageQueries
) : ChatMessageLocalDataSource {

    override fun insert(chatMessageEntity: ChatMessageEntity) {
        chatMessageQueries.insert(
            chatMessageEntity.uuid,
            chatMessageEntity.user_uuid,
            chatMessageEntity.sender_uuid,
            chatMessageEntity.type,
            chatMessageEntity.content,
            chatMessageEntity.user_intent,
            chatMessageEntity.duration,
            chatMessageEntity.timestamp
        )
    }

    override fun upsertByUuid(chatMessageEntity: ChatMessageEntity) {
        throw NotImplementedError()
    }

    override fun findByUuid(uuid: String): ChatMessageEntity? {
        throw NotImplementedError()
    }

    override fun findAll(): List<ChatMessageEntity> {
        throw NotImplementedError()
    }

    override fun findByUserUuid(userUuid: String): List<ChatMessageEntity> {
        return chatMessageQueries.findByUserUuid(userUuid).executeAsList()
    }

    override fun updateByUuid(chatMessageEntity: ChatMessageEntity) {
        chatMessageQueries.updateByUuid(
            uuid = chatMessageEntity.uuid,
            user_uuid = chatMessageEntity.user_uuid,
            sender_uuid = chatMessageEntity.sender_uuid,
            type = chatMessageEntity.type,
            content = chatMessageEntity.content,
            user_intent = chatMessageEntity.user_intent,
            duration = chatMessageEntity.duration,
            timestamp = chatMessageEntity.timestamp,

        )
    }

    override fun deleteByUuid(uuid: String) {
        throw NotImplementedError()
    }


}