package app.penny.feature.myLedger

sealed class MyLedgerIntent {

    data object RefreshLedgers : MyLedgerIntent()


}