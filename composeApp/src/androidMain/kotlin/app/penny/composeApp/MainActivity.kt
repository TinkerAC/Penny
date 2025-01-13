package app.penny.composeApp

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge

import app.penny.platform.getPlatform
import app.penny.platform.initAppContext
import app.penny.platform.requestMicrophonePermission


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initAppContext(
            context = this,
        )


        getPlatform()

        requestMicrophonePermission(activity = this, context = this)

        enableEdgeToEdge() // Add this
        setContent {
            App()
        }
    }
}

