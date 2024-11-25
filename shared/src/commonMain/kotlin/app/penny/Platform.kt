package app.penny

import app.penny.core.data.kvstore.TokenManager
import co.touchlab.kermit.Logger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.dsl.KoinAppDeclaration

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform

expect class ApplicationInitializer(
    application: Any
) : KoinComponent {
    // init logic with expect-actual
    fun initKoin(appDeclaration: KoinAppDeclaration = {}): ApplicationInitializer


}


// initSession: 通用逻辑，返回 ApplicationInitializer 本身
fun ApplicationInitializer.initSession(
): ApplicationInitializer {
    val tokenManager: TokenManager by inject()
    Logger.i { "Initializing session..." }
    CoroutineScope(Dispatchers.Default).launch {
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

//// initialize: 链式调用 initKoin 和 initSession
//fun ApplicationInitializer.initialize(tokenManager: TokenManager): ApplicationInitializer {
//    return this.initKoin().initSession(tokenManager) // 链式调用
//}