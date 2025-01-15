package app.penny.config

object Config {
    //    const val API_URL = "http://100.80.88.1:8080" // tailScale
//    const val API_URL = "http://192.168.31.221:8080" // LAN1

    private val DEFAULT_URL: String
        get() = "http://192.168.1.2:8080" // LAN2

    val API_URL
        get() = DEBUG_URL.ifEmpty { DEFAULT_URL }


    var DEBUG_URL: String = ""
}
