// androidMain/src/main/kotlin/com/example/recorder/AudioRecorderAndroid.kt
package app.penny.platform

import android.media.MediaRecorder
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

actual object AudioRecorderFactory {
    actual fun createAudioRecorder(cacheDir: String): AudioRecorder {
        return AudioRecorderAndroid(cacheDir)
    }
}

class AudioRecorderAndroid(private val cacheDir: String) : AudioRecorder {
    private var mediaRecorder: MediaRecorder? = null
    private var isRecording = false
    private var outputFilePath: String = ""

    override suspend fun startRecording() {
        withContext(Dispatchers.IO) {
            if (isRecording) return@withContext

            val outputFile = File(cacheDir, "recording_${System.currentTimeMillis()}.m4a")
            outputFilePath = outputFile.absolutePath

            mediaRecorder = MediaRecorder().apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setOutputFile(outputFilePath)
                prepare()
                start()
            }

            isRecording = true
        }
    }

    override suspend fun stopRecording(): String {
        return withContext(Dispatchers.IO) {
            if (!isRecording) return@withContext ""

            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null
            isRecording = false
            outputFilePath
        }
    }

    override fun isRecording(): Boolean {
        return isRecording
    }
}
