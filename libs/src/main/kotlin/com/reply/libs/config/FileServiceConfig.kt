package com.reply.libs.config

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

object FileServiceConfig {
    private val mainConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load().getConfig("fileService"))

    val savePath: String = mainConfig.property("path").getString()
}