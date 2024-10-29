package app.penny.di

import app.cash.sqldelight.db.SqlDriver
import app.penny.database.PennyDatabase
import app.penny.data.datasource.local.TransactionLocalDataSource
import app.penny.data.repository.TransactionRepository
import app.penny.data.repository.impl.TransactionRepositoryImpl
import app.penny.domain.usecase.GetTransactionsUseCase
import app.penny.presentation.viewmodel.DashboardViewModel
import app.penny.presentation.viewmodel.TransactionViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

fun commonModule() = module {

    // 提供 PennyDatabase
    single {
        PennyDatabase(get<SqlDriver>())
    }

    // 提供 TransactionsQueries
    single {
        get<PennyDatabase>().transactionsQueries
    }

    // 提供 DataSource
    single { TransactionLocalDataSource(get()) }

    // 提供 Repository
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }

    // 提供 UseCase
    factory { GetTransactionsUseCase(get()) }

    // 注入 ViewModel
    factory { TransactionViewModel(get()) }

    factory { DashboardViewModel() }
}

expect fun platformModule(): Module
