package app.penny.core.data.repository

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
    suspend fun count(): Long

    suspend fun findByEmail(email: String): UserModel?

    suspend fun findByEmailIsNull(): UserModel?

    suspend fun deleteAll()


    suspend fun findCurrentUser(): UserModel?


    suspend fun upsertByUuid(userModel: UserModel)

}