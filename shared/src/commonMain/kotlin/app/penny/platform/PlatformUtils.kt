package app.penny.platform

abstract class Platform(
) {
    abstract val name: String
    abstract val version: String

    override fun toString(): String {
        return "Platform(name='$name', version='$version')"
    }
}

expect fun getPlatform(): Platform
