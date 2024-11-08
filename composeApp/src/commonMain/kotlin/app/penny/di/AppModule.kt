package app.penny.di

import app.cash.sqldelight.db.SqlDriver
import app.penny.data.datasource.local.LedgerLocalDataSource
import app.penny.data.datasource.local.TransactionLocalDataSource
import app.penny.data.repository.LedgerRepository
import app.penny.data.repository.TransactionRepository
import app.penny.data.repository.impl.LedgerRepositoryImpl
import app.penny.data.repository.impl.TransactionRepositoryImpl
import app.penny.database.PennyDatabase
import app.penny.domain.usecase.DeleteLedgerUseCase
import app.penny.domain.usecase.GetAllLedgerUseCase
import app.penny.domain.usecase.GetAllTransactionsUseCase
import app.penny.domain.usecase.GetTransactionByLedgerUseCase
import app.penny.domain.usecase.GetTransactionsUseCase
import app.penny.domain.usecase.InsertLedgerUseCase
import app.penny.domain.usecase.InsertRandomTransactionUseCase
import app.penny.presentation.ui.screens.dashboard.DashboardViewModel
import app.penny.presentation.ui.screens.myLedger.MyLedgerViewModel
import app.penny.presentation.ui.screens.newLedger.NewLedgerViewModel
import app.penny.presentation.ui.screens.newTransaction.NewTransactionViewModel
import app.penny.presentation.ui.screens.transactions.TransactionViewModel
import app.penny.presentation.viewmodel.MainViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

fun commonModule() = module {

    // 提供 PennyDatabase
    single {
        PennyDatabase(get<SqlDriver>())
    }

    // 提供 Queries
    single {
        get<PennyDatabase>().transactionsQueries
    }

    single { get<PennyDatabase>().ledgerQueries }

    // 提供 DataSource
    single { TransactionLocalDataSource(get()) }
    single { LedgerLocalDataSource(get()) }


    // 提供 Repository
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }

    single<LedgerRepository> { LedgerRepositoryImpl(get()) }

    // 提供 UseCase
    factory { GetTransactionsUseCase(get()) }
    factory { InsertLedgerUseCase(get()) }
    factory { GetAllLedgerUseCase(get()) }
    factory { InsertRandomTransactionUseCase(get(), get()) }
    factory { DeleteLedgerUseCase(get()) }
    factory { GetTransactionByLedgerUseCase(get()) }
    factory { GetAllTransactionsUseCase(get()) }

    // 注入 ViewModel
    factory { NewTransactionViewModel(get(), get()) }
    factory { DashboardViewModel(get(), get()) }


    factory { TransactionViewModel(get()) }
    factory { MainViewModel(get(), get()) }
    factory { MyLedgerViewModel(get(), get()) }
    factory { NewLedgerViewModel(get()) }

//    factory { NumPadViewModel() }


    factory { MyLedgerViewModel(get(), get()) }

    factory { NewLedgerViewModel(get()) }


    //Kermit Logger


}

expect fun platformModule(): Module
