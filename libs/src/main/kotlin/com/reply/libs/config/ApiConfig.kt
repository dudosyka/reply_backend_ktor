package com.reply.libs.config

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

data class ApiConfig (
    private val routingConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load().getConfig("routing")),
    val mainEndpoint: String =  routingConfig.property("mainEndpoint").getString(),
    val adminEndpoint: String =  routingConfig.property("adminEndpoint").getString(),
    val clientEndpoint: String =  routingConfig.property("clientEndpoint").getString(),
    val openEndpoint: String =  routingConfig.property("openEndpoint").getString(),
    val authorizedEndpoint: String =  routingConfig.property("authorizedEndpoint").getString(),
)