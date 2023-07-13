package com.reply.libs.config

import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

data class JwtConfig(
    private val jwtConfig: ApplicationConfig = HoconApplicationConfig(ConfigFactory.load().getConfig("jwt")),
    val audience: String = jwtConfig.property("audience").getString(),
    val domain: String = jwtConfig.property("audience").getString(),
    val realm: String = jwtConfig.property("audience").getString(),
    val secret: String = jwtConfig.property("audience").getString(),
    val expiration: Long = jwtConfig.config("expiration").property("seconds").getString().toLong()
)
