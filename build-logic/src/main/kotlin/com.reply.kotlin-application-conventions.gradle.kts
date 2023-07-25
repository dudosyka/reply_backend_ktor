val ktorVersion: String = "2.3.2"
val kotlinVersion: String = "1.9.0"
val logbackVersion: String = "1.2.9"
val prometeusVersion: String = "1.11.1"
val kodeinVersion: String = "7.17.0"
val consulClientVersion: String = "1.5.3"
val exposedVersion = "0.41.1"
val jodaVersion = "2.3"

plugins {
    id("com.reply.kotlin-common-conventions")
    application
    id("io.ktor.plugin")
    id("org.jetbrains.kotlin.plugin.serialization")
}

group = "com.reply"
version = "0.0.1"

repositories {
    mavenCentral()
}

dependencies {
    fun ktor(module: String = "", version: String = ktorVersion) = "io.ktor:ktor-$module:$version"
    fun jet(module: String = "", version: String = kotlinVersion) = "org.jetbrains.kotlin:$module:$version"
    fun exposed(module: String = "", version: String = exposedVersion) = "org.jetbrains.exposed:exposed-$module:$version"

    //Ktor dependencies
    implementation(ktor("server-core-jvm"))
    implementation(ktor("server-auth-jvm"))
    implementation(ktor("server-auth-jwt-jvm"))
    implementation(ktor("server-cors-jvm"))
    implementation(ktor("server-openapi"))
    implementation(ktor("server-swagger-jvm"))
    implementation(ktor("server-call-logging-jvm"))
    implementation(ktor("server-call-id-jvm"))
    implementation(ktor("server-metrics-jvm"))
    implementation(ktor("server-content-negotiation-jvm"))
    implementation(ktor("serialization-kotlinx-json-jvm"))
    implementation(ktor("server-netty-jvm"))
    implementation(ktor("server-request-validation"))
    implementation(ktor("server-metrics-micrometer-jvm"))
    implementation(ktor("server-status-pages"))
    implementation(ktor("server-host-common"))
    implementation(ktor("client-core"))
    implementation(ktor("client-apache"))
    implementation(ktor("client-auth"))
    implementation(ktor("serialization-kotlinx-json"))
    implementation(ktor("client-content-negotiation"))
    testImplementation(ktor("server-tests-jvm"))
    testImplementation(ktor("server-test-host"))


    implementation("com.orbitz.consul:consul-client:$consulClientVersion")

    api(exposed("core"))
    api(exposed("dao"))
    api(exposed("jdbc"))
    api(exposed("java-time"))
    implementation("joda-time:joda-time:$jodaVersion")
    implementation("com.zaxxer:HikariCP:3.4.2")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4")

    //Logs and metrics
    implementation("io.micrometer:micrometer-registry-prometheus:$prometeusVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("org.kodein.di:kodein-di-jvm:$kodeinVersion")

    runtimeOnly(jet("kotlin-reflect"))
    implementation(jet("kotlin-reflect"))
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0-RC")

    implementation("org.mindrot:jbcrypt:0.4")

    testImplementation(jet("kotlin-test"))
}