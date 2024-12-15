package app.penny.core.data.repository.impl

import app.penny.core.data.database.UserLocalDataSource
import app.penny.core.data.kvstore.UserDataManager
import app.penny.core.data.model.toEntity
import app.penny.core.data.model.toModel
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.model.UserModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid


class UserRepositoryImpl(
    private val userLocalDataSource: UserLocalDataSource,
    private val userDataManager: UserDataManager
) : UserRepository {


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

    override suspend fun findByEmail(email: String): UserModel? {
        return userLocalDataSource.findByEmail(email)?.toModel()
    }

    override suspend fun findByEmailIsNull(): UserModel? {
        return userLocalDataSource.findByEmailIsNull().firstOrNull()?.toModel()
    }

    override suspend fun deleteAll() {
        userLocalDataSource.deleteAll()
    }


    override suspend fun findCurrentUser(): UserModel? {
        val activeUserUuid = userDataManager.getStringOrNull(UserDataManager.USER_UUID)
        return activeUserUuid?.let { userLocalDataSource.findByUuid(it)?.toModel() }
    }

    override suspend fun upsertByUuid(userModel: UserModel) {
        userLocalDataSource.upsertByUuid(userModel.toEntity())
    }
}