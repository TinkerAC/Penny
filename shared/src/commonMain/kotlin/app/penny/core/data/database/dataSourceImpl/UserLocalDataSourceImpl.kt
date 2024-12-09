package app.penny.core.data.database.dataSourceImpl

import app.penny.core.data.database.UserLocalDataSource
import app.penny.database.UserEntity
import app.penny.database.UserQueries

class UserLocalDataSourceImpl(
    private val userQueries: UserQueries
) : UserLocalDataSource {

    override fun insert(userEntity: UserEntity) {
        userQueries.insert(
            uuid = userEntity.uuid,
            username = userEntity.username,
            email = userEntity.email
        )
    }

    override fun upsertByUuid(userEntity: UserEntity) {
        userQueries.insert(
            uuid = userEntity.uuid,
            username = userEntity.username,
            email = userEntity.email
        )
    }

    override fun findByUuid(uuid: String): UserEntity? {
        return userQueries.findByUuid(uuid).executeAsOneOrNull()
    }

    override fun count(): Long {
        return userQueries.count().executeAsOne()
    }

    override fun findAll(): List<UserEntity> {
        return userQueries.findAll().executeAsList()
    }

    override fun findByEmail(email: String): UserEntity? {
        return userQueries.findByEmail(email).executeAsOneOrNull()
    }

    override fun findByEmailIsNull(): List<UserEntity> {
        return userQueries.findByEmailIsNull().executeAsList()
    }
}