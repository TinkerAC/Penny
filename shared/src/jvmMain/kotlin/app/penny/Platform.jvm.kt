package app.penny

import app.penny.core.data.kvstore.TokenManager
import kotlinx.coroutines.runBlocking
import org.koin.core.component.KoinComponent
import org.koin.dsl.KoinAppDeclaration

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}


actual fun getPlatform(): Platform = JVMPlatform()

actual class ApplicationInitializer actual constructor(
    private val application: Any
) : KoinComponent {
    actual fun initKoin(
        appDeclaration: KoinAppDeclaration
    ): ApplicationInitializer {
        app.penny.di.initKoin()
        return this
    }


}