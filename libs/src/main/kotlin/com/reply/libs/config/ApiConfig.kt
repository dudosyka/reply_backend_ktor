package com.reply.libs.config

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

data class ApiConfig(
    private val mainConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load().getConfig("api")),
    private val routingConfig: ApplicationConfig = mainConfig.config("routing"),
    private val servicesConfig: ApplicationConfig = mainConfig.config("services"),

    //Main
    val protocol: String = mainConfig.property("protocol").getString(),

    //Endpoints configurations
    val mainEndpoint: String = routingConfig.property("mainEndpoint").getString(),
    val adminEndpoint: String = routingConfig.property("adminEndpoint").getString(),
    val clientEndpoint: String = routingConfig.property("clientEndpoint").getString(),
    val openEndpoint: String = routingConfig.property("openEndpoint").getString(),
    val authorizedEndpoint: String = routingConfig.property("authorizedEndpoint").getString(),

    //Service` hosts configuration
    val blockServiceHost: String = servicesConfig.property("block-service").getString(),
    val companyServiceHost: String = servicesConfig.property("block-service").getString(),
    val deliveryServiceHost: String = servicesConfig.property("block-service").getString(),
    val statServiceHost: String = servicesConfig.property("block-service").getString(),
    val telegramServiceHost: String = servicesConfig.property("block-service").getString(),
    val testServiceHost: String = servicesConfig.property("block-service").getString(),
    val userServiceHost: String = servicesConfig.property("block-service").getString(),
)