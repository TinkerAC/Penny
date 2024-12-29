package app.penny

import android.app.Application

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val appInitializer =
            ApplicationInitializer(
                application = this
            ).initNotifierManager().initKoin().initSession()

    }
}