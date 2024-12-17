package app.penny

import androidx.compose.runtime.ProvidedValue
import org.koin.core.component.KoinComponent
import org.koin.dsl.KoinAppDeclaration

class JVMPlatform() : Platform() {
    override val name: String = "Java"
    override val version: String = { System.getProperty("java.version") }.toString()
}


actual fun getPlatform(): Platform {
    println("getPlatform: ${JVMPlatform()}")

    return JVMPlatform()
}

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

actual fun provideNullAndroidOverscrollConfiguration(): Array<ProvidedValue<*>> {
    return emptyArray<ProvidedValue<*>>()
}

actual fun disableUiKitOverscroll() {
}