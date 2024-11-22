package app.penny.core.data.repository.impl

import app.penny.core.data.database.LedgerLocalDataSource
import app.penny.core.data.model.toEntity
import app.penny.core.data.model.toModel
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.domain.model.LedgerModel

class LedgerRepositoryImpl(

    private val ledgerLocalDataSource: LedgerLocalDataSource
) : LedgerRepository {
    override suspend fun insertLedger(ledgerModel: LedgerModel) {
        return ledgerLocalDataSource.insertLedger(
            ledgerModel.toEntity()
        )
    }

    override suspend fun getLedgerById(ledgerId: Long): LedgerModel {
        return ledgerLocalDataSource.getLedgerById(ledgerId).toModel()
    }

    override suspend fun getAllLedgers(): List<LedgerModel> {
        return ledgerLocalDataSource.getAllLedgers().map { it.toModel() }
    }

    override suspend fun updateLedger(ledgerModel: LedgerModel) {
        ledgerLocalDataSource.updateLedger(ledgerModel.toEntity())
    }


    override suspend fun deleteLedger(ledgerId: Long) {
        ledgerLocalDataSource.deleteLedger(ledgerId)
    }
}