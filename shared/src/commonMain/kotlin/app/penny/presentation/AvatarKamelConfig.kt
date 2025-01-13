package app.penny.presentation

import io.kamel.core.config.KamelConfig
import io.kamel.core.config.httpUrlFetcher
import io.kamel.core.config.takeFrom
import io.kamel.image.config.Default

val AvatarKamelConfig = KamelConfig {
    // Copies the default implementation if needed
    takeFrom(KamelConfig.Default)

    // Configures Ktor HttpClient
    httpUrlFetcher {
        // httpCache is defined in kamel-core and configures the ktor client
        // to install a HttpCache feature with the implementation provided by Kamel.
        // The size of the cache can be defined in Bytes.
        httpCache(20 * 1024 * 1024  /* 10 MiB */)

    }

    // more functionality available.
}
