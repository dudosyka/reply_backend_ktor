package com.reply.gateway

import com.reply.gateway.consul.BlockClient
import com.reply.gateway.consul.TestClient
import com.reply.gateway.controller.AdminController
import com.reply.gateway.controller.AuthorizedController
import com.reply.gateway.controller.ClientController
import com.reply.gateway.controller.OpenController
import com.reply.gateway.consul.UserClient
import com.reply.libs.consul.FileServiceClient
import com.reply.libs.plugins.consul.ConsulServer
import com.reply.libs.utils.kodein.bindSingleton
import com.reply.libs.utils.kodein.kodeinApplication
import com.reply.libs.plugins.*
import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.util.logging.*

fun main() = EngineMain.main(Array(0) { "" })

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureValidation()
    responseFilter()
    kodeinApplication {
        //Consul
        bindSingleton { UserClient(it) }
        bindSingleton { TestClient(it) }
        bindSingleton { BlockClient(it) }
        bindSingleton { FileServiceClient(it) }

        //Controllers
        bindSingleton { AdminController(it) }
        bindSingleton { AuthorizedController(it) }
        bindSingleton { ClientController(it) }
        bindSingleton { OpenController(it) }

        //Logger
        bindSingleton { KtorSimpleLogger("GatewayService") }

        install(ConsulServer) {
            serviceName = "gateway-service"
            host = "localhost"
            port = 80
            consulUrl = "http://localhost:8500"
            registrationConfig {
//                check(Registration.RegCheck.http("$host:$port${ApiConfig().openEndpoint}/user/health", 120))
            }
        }
    }
}
