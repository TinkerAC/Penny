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
            chatMessageEntity.action,
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
        chatMessageQueries.updateByUuid(
            uuid = chatMessageEntity.uuid,
            user_uuid = chatMessageEntity.user_uuid,
            sender_uuid = chatMessageEntity.sender_uuid,
            type = chatMessageEntity.type,
            content = chatMessageEntity.content,
            action = chatMessageEntity.action,
            audio_file_path = chatMessageEntity.audio_file_path,
            duration = chatMessageEntity.duration,
            timestamp = chatMessageEntity.timestamp,
            action_status = chatMessageEntity.action_status

        )
    }

    override fun deleteByUuid(uuid: String) {
        TODO("Not yet implemented")
    }


}