package com.reply.libs.config

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

object JwtConfig {
    private val jwtConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load().getConfig("jwt"))
    val domain: String = jwtConfig.property("domain").getString()
    val secret: String = jwtConfig.property("secret").getString()
    val expiration: Long = jwtConfig.config("expiration").property("seconds").getString().toLong()
}
