package app.penny.core.data.database

import app.penny.database.LedgerEntity
import app.penny.database.LedgerQueries

class LedgerLocalDataSource(
    private val ledgerQueries: LedgerQueries

) {

    fun insertLedger(ledgerEntity: LedgerEntity) {
        ledgerQueries.insertLedger(
            name = ledgerEntity.name,
            currency_code = ledgerEntity.currency_code,
            cover_name = ledgerEntity.cover_name,
            description = ledgerEntity.description,
        )
    }

    fun getLedgerById(id: Long): LedgerEntity {
        return ledgerQueries.getLedgerById(id).executeAsOne()
    }

    fun getAllLedgers(): List<LedgerEntity> {
        return ledgerQueries.getAllLedgers().executeAsList()
    }


    fun updateLedger(ledgerEntity: LedgerEntity) {
        ledgerQueries.updateLedger(
            name = ledgerEntity.name,
            currency_code = ledgerEntity.currency_code,
            id = ledgerEntity.id
        )
    }


    fun deleteLedger(id: Long) {
        ledgerQueries.deleteLedger(id)
    }


}