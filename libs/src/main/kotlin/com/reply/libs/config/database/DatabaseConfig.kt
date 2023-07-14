package com.reply.libs.config.database

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

data class DatabaseConfig (
    private val mainConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load().getConfig("database")),
    val url: String = mainConfig.property("url").getString(),
    val driver: String = mainConfig.property("driver").getString(),
    val user: String = mainConfig.property("user").getString(),
    val password: String = mainConfig.property("password").getString()
)