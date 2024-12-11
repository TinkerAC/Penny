package app.penny

import app.penny.core.data.kvstore.TokenManager
import app.penny.core.data.repository.LedgerRepository
import app.penny.core.data.repository.UserDataRepository
import app.penny.core.data.repository.UserRepository
import app.penny.core.domain.usecase.InitLocalUserUseCase
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.dsl.KoinAppDeclaration

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import kotlin.uuid.ExperimentalUuidApi

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect class
ApplicationInitializer(
    application: Any? = null
) : KoinComponent {
    // init logic with expect-actual
    fun initKoin(appDeclaration: KoinAppDeclaration = {}): ApplicationInitializer


}


// initSession: 通用逻辑，返回 ApplicationInitializer 本身
fun ApplicationInitializer.initSession(
): ApplicationInitializer {
    val userDataRepository: UserDataRepository by inject()
    val tokenManager: TokenManager by inject()

    CoroutineScope(Dispatchers.Default).launch {
        val isFirstTime = userDataRepository.getIsFirstTime()

        if (isFirstTime) {
            Logger.i("First time launch, skip session initialization")
            return@launch // 如果是第一次启动，跳过初始化 session
        } //TODO: 实现此逻辑
        Logger.i { "Initializing session..." }
        try {
            val accessToken = tokenManager.getAccessToken()
            Logger.i { "Access token loaded successfully: $accessToken" }
        } catch (e: IllegalStateException) {
            Logger.w(e) { "Failed to get access token" }
            // 根据需求处理异常
        }
    }
    return this // 返回自身

}

@OptIn(ExperimentalUuidApi::class)
fun ApplicationInitializer.initUser(): ApplicationInitializer {
    //如果数据库中没有用户信息,创建默认用户

    val userRepository: UserRepository by inject()
    val userDataRepository: UserDataRepository by inject()
    val initLocalUserUseCase: InitLocalUserUseCase by inject() //TODO: 为OnBoarding 创建viewModel,并在那里调用
    CoroutineScope(Dispatchers.Default).launch {
//        initLocalUserUseCase()
    }
    return this


}





// initialize: 链式调用 initKoin 和 initSession
fun ApplicationInitializer.initialize(): ApplicationInitializer {
    return this.initKoin().initSession()
}