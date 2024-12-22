package app.penny.core.data.database.dataSourceImpl

import app.penny.core.data.database.LedgerLocalDataSource
import app.penny.database.LedgerEntity
import app.penny.database.LedgerQueries

class LedgerLocalDataSourceImpl(
    private val ledgerQueries: LedgerQueries
) : LedgerLocalDataSource {

    override fun upsertByUuid(ledgerEntity: LedgerEntity) {
        ledgerQueries.upsertByUuid(
            user_uuid = ledgerEntity.user_uuid,
            uuid = ledgerEntity.uuid,
            name = ledgerEntity.name,
            currency_code = ledgerEntity.currency_code,
            cover_name = ledgerEntity.cover_name,
            description = ledgerEntity.description
        )
    }

    override fun findByUuid(uuid: String): LedgerEntity? {
        return ledgerQueries.findByUuid(uuid).executeAsOneOrNull()
    }

    override fun findAll(): List<LedgerEntity> {
        return ledgerQueries.findAll().executeAsList()
    }

    override fun findByUserUuid(userUuid: String): List<LedgerEntity> {
        return ledgerQueries.findByUserUuid(userUuid).executeAsList()
    }

    override fun updateByUuid(ledgerEntity: LedgerEntity) {
        ledgerQueries.update(
            name = ledgerEntity.name,
            currency_code = ledgerEntity.currency_code,
            cover_name = ledgerEntity.cover_name,
            description = ledgerEntity.description,
            uuid = ledgerEntity.uuid
        )
    }

    override fun deleteByUuid(uuid: String) {
        ledgerQueries.deleteByUuid(uuid)
    }


    override fun countByUserUuid(userUuid: String): Long {
        return ledgerQueries.countByUserUuid(userUuid).executeAsOne()
    }


    override fun insert(ledgerEntity: LedgerEntity) {


        ledgerQueries.insert(
            user_uuid = ledgerEntity.user_uuid,
            uuid = ledgerEntity.uuid,
            name = ledgerEntity.name,
            currency_code = ledgerEntity.currency_code,
            cover_name = ledgerEntity.cover_name,
            description = ledgerEntity.description
        )
    }


    override fun findByUserUuidAndUpdatedAtAfter(
        userUuid: String,
        timestamp: Long
    ): List<LedgerEntity> {
        return ledgerQueries.findByUserUuidAndUpdatedAtAfter(
            user_uuid = userUuid,
            updated_at = timestamp
        ).executeAsList()
    }


    override fun countByUserUuidAndUpdatedAtAfter(toString: String, epochSeconds: Long): Long {
        return ledgerQueries.countByUserUuidAndUpdatedAtAfter(
            user_uuid = toString,
            updated_at = epochSeconds
        ).executeAsOne()
    }


}