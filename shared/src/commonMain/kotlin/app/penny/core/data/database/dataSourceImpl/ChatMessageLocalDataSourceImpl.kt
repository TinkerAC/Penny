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
            chatMessageEntity.audio_file_path,
            chatMessageEntity.duration,
            chatMessageEntity.timestamp
        )
    }

    override fun upsertByUuid(chatMessageEntity: ChatMessageEntity) {
        TODO("Not yet implemented")
    }

    override fun findByUuid(uuid: String): ChatMessageEntity? {
        TODO("Not yet implemented")
    }

    override fun findAll(): List<ChatMessageEntity> {
        TODO("Not yet implemented")
    }

    override fun findByUserUuid(userUuid: String): List<ChatMessageEntity> {
        return chatMessageQueries.findByUserUuid(userUuid).executeAsList()
    }

    override fun updateByUuid(chatMessageEntity: ChatMessageEntity) {
        TODO("Not yet implemented")
    }

    override fun deleteByUuid(uuid: String) {
        TODO("Not yet implemented")
    }
}