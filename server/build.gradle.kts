plugins {
    alias(libs.plugins.kotlinJvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlinxSerialization)

    application

    //serialization plugin （libs）

}

group = "app.penny"
version = "1.0.0"
application {
    mainClass.set("app.penny.ApplicationKt")
    applicationDefaultJvmArgs =
        listOf("-Dio.ktor.development=${extra["io.ktor.development"] ?: "false"}")
}

dependencies {

    implementation(projects.shared)
    implementation(libs.logback)
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.client.content.negotiation)
    runtimeOnly(libs.ktor.ktor.client.okhttp)

//    testImplementation(libs.ktor.server.tests)
    testImplementation(libs.kotlin.test.junit)

    //exposed (ORM)

    implementation(libs.exposed.core)

    implementation(libs.exposed.crypt)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)

    implementation(libs.exposed.kotlin.datetime)

    implementation(libs.exposed.json)
    implementation(libs.exposed.money)
    implementation(libs.exposed.spring.boot.starter)

    //crypto
    implementation(libs.jbcrypt)

    //jwt
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)


    //serialization
    implementation(libs.ktor.serialization.kotlinx.json.jvm)
    implementation(libs.ktor.server.content.negotiation.jvm)

    //mysql
    implementation(libs.mysql.connector.java)
    implementation(libs.openai.client)


}