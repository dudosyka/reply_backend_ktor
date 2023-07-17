package com.reply.libs.config

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

object ApiConfig {
    private val mainConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load().getConfig("api"))
    private val routingConfig: ApplicationConfig = mainConfig.config("routing")
    private val servicesConfig: ApplicationConfig = mainConfig.config("services")

    //Main
    val protocol: String = mainConfig.property("protocol").getString()

    //Endpoints configurations
    val mainEndpoint: String = routingConfig.property("mainEndpoint").getString()
    val adminEndpoint: String = routingConfig.property("adminEndpoint").getString()
    val clientEndpoint: String = routingConfig.property("clientEndpoint").getString()
    val openEndpoint: String = routingConfig.property("openEndpoint").getString()
    val authorizedEndpoint: String = routingConfig.property("authorizedEndpoint").getString()

    //Service` hosts configuration
    val blockServiceName: String = servicesConfig.property("block-service").getString()
    val deliveryServiceName: String = servicesConfig.property("delivery-service").getString()
    val statServiceName: String = servicesConfig.property("stat-service").getString()
    val telegramServiceName: String = servicesConfig.property("telegram-service").getString()
    val testServiceName: String = servicesConfig.property("test-service").getString()
    val userServiceName: String = servicesConfig.property("user-service").getString()
    val fileServiceName: String = servicesConfig.property("file-service").getString()
}