package app.penny.core.data.database

import app.penny.database.LedgerEntity
import app.penny.database.LedgerQueries

class LedgerLocalDataSource(
    private val ledgerQueries: LedgerQueries

) {

    fun insertLedger(ledgerEntity: LedgerEntity) {
        ledgerQueries.insertLedger(
            user_uuid = ledgerEntity.user_uuid,
            uuid = ledgerEntity.uuid,
            name = ledgerEntity.name,
            currency_code = ledgerEntity.currency_code,
            cover_name = ledgerEntity.cover_name,
            description = ledgerEntity.description,
        )
    }


    fun getAllLedgers(): List<LedgerEntity> {
        return ledgerQueries.getAllLedgers().executeAsList()
    }


    fun updateLedger(ledgerEntity: LedgerEntity) {
        ledgerQueries.updateLedger(
            name = ledgerEntity.name,
            currency_code = ledgerEntity.currency_code,
            cover_name = ledgerEntity.cover_name,
            description = ledgerEntity.description,
            uuid = ledgerEntity.uuid
        )
    }


    fun deleteLedger(uuid: String) {
        ledgerQueries.deleteLedger(uuid)
    }

    fun getLedgersUpdatedAfter(timestamp: Long): List<LedgerEntity> {
        return ledgerQueries.getLedgersUpdatedAfter(timestamp).executeAsList()
    }


    fun upsertLedgerByUuid(ledgerEntity: LedgerEntity): Unit {
        ledgerQueries.upsertLedgerByUuid(
            user_uuid = ledgerEntity.user_uuid,
            uuid = ledgerEntity.uuid,
            name = ledgerEntity.name,
            currency_code = ledgerEntity.currency_code,
            cover_name = ledgerEntity.cover_name,
            description = ledgerEntity.description,
        )
    }


    fun countLedgersAfter(timeStamp: Long): Int {
        return ledgerQueries.countLedgersUpdatedAfter(timeStamp).executeAsOne().toInt()
    }

    fun getLedgerByUuid(uuid: String): LedgerEntity? {
        return ledgerQueries.getLedgerByUuid(uuid).executeAsOneOrNull()
    }


}