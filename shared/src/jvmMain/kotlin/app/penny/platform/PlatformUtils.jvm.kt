package app.penny.platform

class JVMPlatform() : Platform() {
    override val name: String = "Java"
    override val version: String = System.getProperty("java.version")
}


actual fun getPlatform(): Platform {
    return JVMPlatform()
}
