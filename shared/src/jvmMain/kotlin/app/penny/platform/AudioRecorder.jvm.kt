// jvmMain/src/main/kotlin/com/example/recorder/AudioRecorderJVM.kt
package app.penny.platform

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.sound.sampled.AudioFileFormat
import javax.sound.sampled.AudioFormat
import javax.sound.sampled.AudioInputStream
import javax.sound.sampled.AudioSystem
import javax.sound.sampled.DataLine
import javax.sound.sampled.TargetDataLine

actual object AudioRecorderFactory {
    actual fun createAudioRecorder(cacheDir: String): AudioRecorder {
        return AudioRecorderJVM(cacheDir)
    }
}

fun listSupportedAudioFormats() {
    val mixers = AudioSystem.getMixerInfo()
    for (mixerInfo in mixers) {
        val mixer = AudioSystem.getMixer(mixerInfo)
        val targetLines = mixer.getTargetLineInfo()
        for (lineInfo in targetLines) {
            if (lineInfo is DataLine.Info && TargetDataLine::class.java.isAssignableFrom(lineInfo.lineClass)) {
                val formats = lineInfo.formats
                println("Mixer: ${mixerInfo.name}")
                println("Supported Formats:")
                for (format in formats) {
                    println("\tSample Rate: ${format.sampleRate}, Sample Size: ${format.sampleSizeInBits}, Channels: ${format.channels}, Encoding: ${format.encoding}")
                }
                println()
            }
        }
    }
}

class AudioRecorderJVM(private val cacheDir: String) : AudioRecorder {
    private var targetDataLine: TargetDataLine? = null
    private var isRecordingFlag = false
    private var outputFilePath: String = ""

    override suspend fun startRecording() {

//        listSupportedAudioFormats()

        withContext(Dispatchers.IO) {
            if (isRecordingFlag) return@withContext

            val fileName = "recording_temp.wav"
            val file = File(cacheDir, fileName)
            outputFilePath = file.absolutePath

            val audioFormat = AudioFormat(
                AudioFormat.Encoding.PCM_SIGNED,
                8000.0f,
                16,
                1,
                2,
                8000.0f,
                false
            )

            val info = DataLine.Info(TargetDataLine::class.java, audioFormat)
            if (!AudioSystem.isLineSupported(info)) {
                throw IllegalStateException("Line not supported")
            }

            targetDataLine = AudioSystem.getLine(info) as TargetDataLine
            targetDataLine?.open(audioFormat)
            targetDataLine?.start()

            isRecordingFlag = true

            // Start a new thread to capture the audio
            Thread {
                val audioStream = AudioInputStream(targetDataLine)
                try {
                    AudioSystem.write(audioStream, AudioFileFormat.Type.WAVE, file)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }.start()
        }
    }

    override suspend fun stopRecording(): String {
        return withContext(Dispatchers.IO) {
            if (!isRecordingFlag) return@withContext ""

            targetDataLine?.stop()
            targetDataLine?.close()
            targetDataLine = null
            isRecordingFlag = false
            outputFilePath
        }
    }

    override fun isRecording(): Boolean {
        return isRecordingFlag
    }
}
