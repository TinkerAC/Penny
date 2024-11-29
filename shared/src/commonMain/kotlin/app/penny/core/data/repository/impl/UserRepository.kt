package app.penny.core.data.repository.impl

import app.penny.core.domain.model.UserModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid




@OptIn(ExperimentalUuidApi::class)
interface UserRepository {
    suspend fun insert(userModel: UserModel)
    suspend fun findByUuid(userUuid: Uuid): UserModel?
    suspend fun findAll(): List<UserModel>
    suspend fun update(userModel: UserModel)
    suspend fun deleteByUuid(userUuid: Uuid)


}