package app.penny.platform

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import coil3.PlatformContext

fun requestMicrophonePermission(activity: Activity, context: PlatformContext) {
    if (ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) != PackageManager.PERMISSION_GRANTED
    ) {
        ActivityCompat.requestPermissions(activity, arrayOf(Manifest.permission.RECORD_AUDIO), 1)
    } else {
        Toast.makeText(context, "Permission already granted", Toast.LENGTH_SHORT).show()
    }
}