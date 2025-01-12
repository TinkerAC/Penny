package app.penny.composeApp

import android.app.Application
import app.penny.platform.ApplicationInitializer
import app.penny.platform.initSession


class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        val appInitializer =
            ApplicationInitializer(
                application = this
            ).initNotifierManager().initKoin().initSession()

    }
}