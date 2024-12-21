package app.penny.feature.myLedger

import app.penny.core.domain.model.LedgerModel

sealed class MyLedgerIntent {

    class SetDefaultLedger(val ledger: LedgerModel) : MyLedgerIntent()


}