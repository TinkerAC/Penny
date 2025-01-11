package app.penny.platform

import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.AVFAudio.AVAudioQuality
import platform.AVFAudio.AVAudioRecorder
import platform.CoreAudioTypes.kAudioFormatMPEG4AAC
import platform.Foundation.NSURL
import platform.Foundation.NSUUID

actual object AudioRecorderFactory {
    actual fun createAudioRecorder(cacheDir: String): AudioRecorder {
        return AudioRecorderIOS(cacheDir)
    }
}

class AudioRecorderIOS(private val cacheDir: String) : AudioRecorder {
    private var audioRecorder: AVAudioRecorder? = null
    private var isRecordingFlag = false
    private var outputFilePath: String = ""

    @OptIn(ExperimentalForeignApi::class)
    override suspend fun startRecording() {
        withContext(Dispatchers.Default) {
            if (isRecordingFlag) return@withContext

            val fileName = "recording_${NSUUID.UUID().UUIDString()}.m4a"
            val filePath = "$cacheDir/$fileName"
            outputFilePath = filePath

            val settings = mapOf(
                "AVFormatIDKey" to kAudioFormatMPEG4AAC,
                "AVSampleRateKey" to 12000,
                "AVNumberOfChannelsKey" to 1,
                "AVEncoderAudioQualityKey" to AVAudioQuality.MAX_VALUE,
            ) as Map<Any?, *>

            val url = NSURL.fileURLWithPath(filePath)
            audioRecorder = AVAudioRecorder(url, settings = settings, error = null)

            audioRecorder?.prepareToRecord()
            audioRecorder?.record()

            isRecordingFlag = true
        }
    }

    override suspend fun stopRecording(): String {
        return withContext(Dispatchers.Default) {
            if (!isRecordingFlag) return@withContext ""

            audioRecorder?.stop()
            audioRecorder = null
            isRecordingFlag = false
            outputFilePath
        }
    }

    override fun isRecording(): Boolean {
        return isRecordingFlag
    }
}
