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

    //Ktor dependencies
    implementation(ktor("server-core-jvm", ktorVersion))
    implementation(ktor("server-auth-jvm", ktorVersion))
    implementation(ktor("server-auth-jwt-jvm", ktorVersion))
    implementation(ktor("server-cors-jvm", ktorVersion))
    implementation(ktor("server-openapi", ktorVersion))
    implementation(ktor("server-swagger-jvm", ktorVersion))
    implementation(ktor("server-call-logging-jvm", ktorVersion))
    implementation(ktor("server-call-id-jvm", ktorVersion))
    implementation(ktor("server-metrics-jvm", ktorVersion))
    implementation(ktor("server-content-negotiation-jvm", ktorVersion))
    implementation(ktor("serialization-kotlinx-json-jvm", ktorVersion))
    implementation(ktor("server-netty-jvm", ktorVersion))
    implementation(ktor("server-request-validation", ktorVersion))
    implementation(ktor("server-metrics-micrometer-jvm", ktorVersion))
    implementation(ktor("server-status-pages", ktorVersion))
    implementation(ktor("server-host-common", ktorVersion))
    implementation(ktor("client-core", ktorVersion))
    implementation(ktor("client-apache", ktorVersion))
    implementation(ktor("serialization-kotlinx-json", ktorVersion))
    implementation(ktor("client-content-negotiation", ktorVersion))
    testImplementation(ktor("server-tests-jvm", ktorVersion))


    implementation("com.orbitz.consul:consul-client:$consulClientVersion")

    api("org.jetbrains.exposed:exposed-core:$exposedVersion")
    api("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    api("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    api("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
    implementation("joda-time:joda-time:$jodaVersion")
    implementation("com.zaxxer:HikariCP:3.4.2")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.1.4")
//    implementation("mysql", "mysql-connector-java","8.0.32")

    //Logs and metrics
    implementation("io.micrometer:micrometer-registry-prometheus:$prometeusVersion")
    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("org.kodein.di:kodein-di-jvm:$kodeinVersion")

    runtimeOnly("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlinVersion")
}