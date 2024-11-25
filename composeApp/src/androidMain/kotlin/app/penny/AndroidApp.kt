package app.penny

import android.app.Application
import app.penny.core.data.kvstore.TokenManager
import org.koin.android.ext.android.inject

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        ApplicationInitializer(
            application = this)
            .initKoin()
            .initSession()


    }
}