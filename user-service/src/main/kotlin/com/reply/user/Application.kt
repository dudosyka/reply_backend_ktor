package com.reply.user

import com.reply.libs.consul.server.ConsulFeature
import com.reply.libs.kodein.bindSingleton
import com.reply.libs.kodein.kodeinApplication
import com.reply.libs.plugins.*
import com.reply.user.controller.AuthController
import com.reply.user.service.AuthService
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
        bindSingleton { AuthController(it) }
        bindSingleton { AuthService() }
        bindSingleton { KtorSimpleLogger("UserService") }
    }
    install(ConsulFeature) {
        serviceName = "user"
        host = "localhost"
        port = (this@module.environment as ApplicationEngineEnvironment).connectors[0].port
        consulUrl = "http://localhost:8500"
        config {
            port = (environment as ApplicationEngineEnvironment).connectors.first().port
        }
        registrationConfig {
//            check(Registration.RegCheck.http("$host:$port${ApiConfig().openEndpoint}/user/health", 120))
        }
    }
//    environment.monitor.subscribe(ServerReady) { app ->
//    }
}
