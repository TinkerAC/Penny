package app.penny.composeApp

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import app.penny.platform.getPlatform
import app.penny.platform.initMokoResources

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initMokoResources(
            context = this,
        )
        getPlatform()
        enableEdgeToEdge() // Add this
        setContent {
            App()
        }
    }
}

