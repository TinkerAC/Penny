package app.penny

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform