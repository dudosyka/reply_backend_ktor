package com.reply.libs.test

import com.reply.libs.config.ApiConfig
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

open class BaseTest {
    protected val openEndpoint = "http://localhost${ApiConfig.openEndpoint}/"
    protected val authorizedEndpoint = "http://localhost${ApiConfig.authorizedEndpoint}"
    protected val clientEndpoint = "http://localhost${ApiConfig.clientEndpoint}"
    protected val adminEndpoint = "http://localhost${ApiConfig.adminEndpoint}"

    protected open var client = HttpClient(Apache) {
        install(ContentNegotiation) {
            json()
        }
    }
}