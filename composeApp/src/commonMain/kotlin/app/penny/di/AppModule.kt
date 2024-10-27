package app.penny.di

import app.penny.data.datasource.Database
import app.penny.data.datasource.TransactionLocalDataSource
import app.penny.data.datasource.TransactionLocalDataSourceImpl
import app.penny.data.repository.TransactionRepository
import app.penny.data.repository.TransactionRepositoryImpl
import app.penny.domain.usecase.GetTransactionsUseCase
import app.penny.presentation.viewmodel.TransactionViewModel
import org.koin.core.module.Module
import org.koin.dsl.module

fun commonModule(): Module = module {

    // 提供 Database
    single { Database(get()) }

//    // 提供 HttpClient
//    single { createHttpClient() }

    // 提供 DataSource
    single<TransactionLocalDataSource> {
        TransactionLocalDataSourceImpl(get())
    }
//    single<TransactionRemoteDataSource> { TransactionRemoteDataSourceImpl(get()) }

    // 提供 Repository
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }

    // 提供 UseCase
    factory { GetTransactionsUseCase(get()) }

    // 注入 ViewModel
    factory { TransactionViewModel(get()) }
}

expect fun platformModule(): Module