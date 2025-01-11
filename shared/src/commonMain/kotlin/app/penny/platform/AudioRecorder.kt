package app.penny.platform

interface AudioRecorder {
    suspend fun startRecording()
    suspend fun stopRecording(): String // 返回录音文件的路径
    fun isRecording(): Boolean
}

// 定义 expect 对象或工厂方法来获取平台特定的实现
expect object AudioRecorderFactory {
    fun createAudioRecorder(cacheDir: String): AudioRecorder
}