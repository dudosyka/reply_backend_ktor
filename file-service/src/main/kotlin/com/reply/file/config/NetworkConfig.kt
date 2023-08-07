package com.reply.file.config

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

object NetworkConfig {
    private val config: HoconApplicationConfig = HoconApplicationConfig(ConfigFactory.load().getConfig("ktor"))

    val port: Int = config.config("deployment").property("port").getString().toInt()
}