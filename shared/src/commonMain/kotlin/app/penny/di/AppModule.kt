package app.penny.di

import app.cash.sqldelight.db.SqlDriver
import app.penny.core.data.database.LedgerLocalDataSource
import app.penny.core.data.database.TransactionLocalDataSource
import app.penny.core.data.kvstore.SessionManager
import app.penny.core.data.kvstore.UserDataManager
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.TransactionRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.impl.LedgerRepositoryImpl
import app.penny.core.data.repository.impl.TransactionRepositoryImpl
import app.penny.core.data.repository.impl.UserDataRepositoryImpl
import app.penny.core.domain.usecase.CheckIsUsernameValidUseCase
import app.penny.core.domain.usecase.DeleteLedgerUseCase
import app.penny.core.domain.usecase.GetAllLedgerUseCase
import app.penny.core.domain.usecase.GetAllTransactionsUseCase
import app.penny.core.domain.usecase.GetTransactionsByLedgerUseCase
import app.penny.core.domain.usecase.InsertLedgerUseCase
import app.penny.core.domain.usecase.InsertRandomTransactionUseCase
import app.penny.core.domain.usecase.LoginUseCase
import app.penny.core.domain.usecase.SearchTransactionsUseCase
import app.penny.core.domain.usecase.UploadUpdatedLedgersUseCase
import app.penny.core.network.ApiClient
import app.penny.database.PennyDatabase
import app.penny.feature.analytics.AnalyticViewModel
import app.penny.feature.dashboard.DashboardViewModel
import app.penny.feature.myLedger.MyLedgerViewModel
import app.penny.feature.newLedger.NewLedgerViewModel
import app.penny.feature.newTransaction.NewTransactionViewModel
import app.penny.feature.profile.ProfileViewModel
import app.penny.feature.transactions.TransactionViewModel
import app.penny.presentation.viewmodel.MainViewModel
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
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


    //SettingManager
    single { UserDataManager(get()) }
    single { SessionManager(get()) }

    // 提供 Repository
    single<TransactionRepository> { TransactionRepositoryImpl(get()) }

    single<LedgerRepository> { LedgerRepositoryImpl(get()) }

    single<UserDataRepository> { UserDataRepositoryImpl(get()) }


    //注入ApiClient
    // Koin 模块定义
    single {
        HttpClient(CIO) {
            install(ContentNegotiation) {
                json(
                    Json {
                        ignoreUnknownKeys = true // 忽略未知字段
                        isLenient = true // 宽松模式，支持非标准 JSON
                    }
                )
            }
        }
    }


    single { ApiClient(get()) }

    // 提供 UseCase
    factory { InsertLedgerUseCase(get()) }
    factory { GetAllLedgerUseCase(get()) }
    factory { InsertRandomTransactionUseCase(get(), get()) }
    factory { DeleteLedgerUseCase(get()) }
    factory { GetTransactionsByLedgerUseCase(get()) }
    factory { GetAllTransactionsUseCase(get()) }
    factory { SearchTransactionsUseCase(get()) }
    factory { CheckIsUsernameValidUseCase(get()) }
    factory { LoginUseCase(get(), get()) }
    factory { UploadUpdatedLedgersUseCase(get(), get(), get()) }
    factory { LoginUseCase(get(), get()) }


    // 注入 ViewModel
    factory { NewTransactionViewModel(get(), get()) }
    factory { DashboardViewModel(get(), get(), get()) }

    factory { AnalyticViewModel(get(), get(), get()) }
    factory { TransactionViewModel(get()) }
    factory { MainViewModel(get(), get()) }
    factory { MyLedgerViewModel(get(), get()) }
    factory { NewLedgerViewModel(get()) }
    factory { ProfileViewModel(get(), get(), get()) }

    factory { MyLedgerViewModel(get(), get()) }

    factory { NewLedgerViewModel(get()) }


}

expect fun platformModule(): Module
