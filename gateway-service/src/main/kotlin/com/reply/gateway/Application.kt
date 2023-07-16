package com.reply.gateway

import com.reply.gateway.controller.AdminController
import com.reply.gateway.controller.AuthorizedController
import com.reply.gateway.controller.ClientController
import com.reply.gateway.controller.OpenController
import com.reply.gateway.consul.UserClient
import com.reply.libs.plugins.consul.ConsulServer
import com.reply.libs.utils.kodein.bindSingleton
import com.reply.libs.utils.kodein.kodeinApplication
import com.reply.libs.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.logging.*

fun main() = EngineMain.main(Array<String>(0) { "" })

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureValidation()
    responseFilter()
    kodeinApplication {
        bindSingleton { KtorSimpleLogger("GatewayService") }
        bindSingleton { AdminController(it) }
        bindSingleton { AuthorizedController(it) }
        bindSingleton { ClientController(it) }
        bindSingleton { OpenController(it) }
        bindSingleton { UserClient(it) }

        install(ConsulServer) {
            serviceName = "gateway-service"
            host = "localhost"
            port = (this@module.environment as ApplicationEngineEnvironment).connectors[0].port
            consulUrl = "http://localhost:8500"
            config {
                port = (environment as ApplicationEngineEnvironment).connectors.first().port
            }
            registrationConfig {
//                check(Registration.RegCheck.http("$host:$port${ApiConfig().openEndpoint}/user/health", 120))
            }
        }
    }
}
