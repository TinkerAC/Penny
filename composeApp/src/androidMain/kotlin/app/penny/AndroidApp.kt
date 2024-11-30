package app.penny

import android.app.Application

class AndroidApp : Application() {
    override fun onCreate() {
        super.onCreate()

        ApplicationInitializer(
            application = this)
            .initialize()


    }
}