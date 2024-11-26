package app.penny

import org.koin.core.component.KoinComponent
import org.koin.dsl.KoinAppDeclaration

class JVMPlatform : Platform {
    override val name: String = "Java ${System.getProperty("java.version")}"
}


actual fun getPlatform(): Platform = JVMPlatform()

actual class ApplicationInitializer actual constructor(
    val application: Any?
) : KoinComponent {
    actual fun initKoin(
        appDeclaration: KoinAppDeclaration
    ): ApplicationInitializer {
        app.penny.di.initKoin()
        return this
    }


}