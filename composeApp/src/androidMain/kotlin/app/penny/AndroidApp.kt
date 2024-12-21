package app.penny

import android.app.Application

//Android entry point
class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()
        val appInitializer =
            ApplicationInitializer(
                application = this
            ).initialize()

    }
}