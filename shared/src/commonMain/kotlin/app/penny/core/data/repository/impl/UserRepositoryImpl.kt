package app.penny.core.data.repository.impl

import app.penny.core.data.database.UserLocalDataSource
import app.penny.core.data.model.toEntity
import app.penny.core.data.model.toModel
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.model.UserModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class UserRepositoryImpl(
    private val userLocalDataSource: UserLocalDataSource
) : UserRepository {
    @OptIn(ExperimentalUuidApi::class)
    override suspend fun insert(userModel: UserModel) {
        userLocalDataSource.insert(userModel.toEntity())
    }

    @ExperimentalUuidApi
    override suspend fun findByUuid(userUuid: Uuid): UserModel? {
        return userLocalDataSource.findByUuid(userUuid.toString())?.toModel()

    }

    override suspend fun findAll(): List<UserModel> {
        return userLocalDataSource.findAll().map { it.toModel() }
    }

    override suspend fun update(userModel: UserModel) {
        TODO("Not yet implemented")
    }

    @ExperimentalUuidApi
    override suspend fun deleteByUuid(userUuid: Uuid) {
        TODO("Not yet implemented")
    }

    override suspend fun count(): Long {
        return userLocalDataSource.count()

    }
}