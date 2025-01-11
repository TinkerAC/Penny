package app.penny.platform

import okio.FileSystem

actual val fileSystem: FileSystem
    get() = FileSystem.SYSTEM