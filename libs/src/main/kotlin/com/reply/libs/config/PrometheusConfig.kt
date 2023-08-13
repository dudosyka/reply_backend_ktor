package com.reply.libs.config

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

object PrometheusConfig {
    private val prometheusConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load().getConfig("prometheus"))
    private val filesConfig = prometheusConfig.config("files")
    private val jobsConfig = prometheusConfig.config("jobs")

    val userServiceTargetsPath = filesConfig.property("userServicePath").getString()
    val testServiceTargetsPath = filesConfig.property("testServicePath").getString()
    val fileServiceTargetsPath = filesConfig.property("fileServicePath").getString()
    val gatewayServiceTargetsPath = filesConfig.property("gatewayServicePath").getString()

    val userServiceJob = jobsConfig.property("userServiceJob").getString()
    val testServiceJob = jobsConfig.property("testServiceJob").getString()
    val fileServiceJob = jobsConfig.property("fileServiceJob").getString()
    val gatewayServiceJob = jobsConfig.property("gatewayServiceJob").getString()


}