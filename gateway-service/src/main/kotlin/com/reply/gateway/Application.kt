package com.reply.gateway

import com.orbitz.consul.model.agent.Registration
import com.reply.gateway.consul.StatClient
import com.reply.gateway.consul.TestClient
import com.reply.gateway.consul.UserClient
import com.reply.gateway.controller.*
import com.reply.libs.config.ApiConfig
import com.reply.libs.consul.FileServiceClient
import com.reply.libs.plugins.consul.ConsulServer
import com.reply.libs.utils.kodein.bindSingleton
import com.reply.libs.utils.kodein.kodeinApplication
import com.reply.libs.plugins.*
import com.reply.libs.utils.consul.health.HealthCheckController
import com.reply.libs.utils.consul.health.ServiceStatus
import com.reply.libs.utils.consul.health.Status
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
        bindSingleton { FileServiceClient(it) }
        bindSingleton { StatClient(it) }

        //Controllers
        bindSingleton { AdminController(it) }
        bindSingleton { AuthorizedController(it) }
        bindSingleton { ClientController(it) }
        bindSingleton { OpenController(it) }
        bindSingleton { HealthCheckController(it) { ServiceStatus(Status.OK, "") } }
        bindSingleton { MetricsController(it) }

        //Logger
        bindSingleton { KtorSimpleLogger("GatewayService") }

        environment.monitor.subscribe(ServerReady) { application ->
            application.apply {
                install(ConsulServer) {
                    serviceName = "gateway-service"
                    host = "localhost"
                    port = 80
                    consulUrl = "http://localhost:8500"
                    registrationConfig {
                        check(
                            Registration.RegCheck.http(
                                "http://host.docker.internal:$port${ApiConfig.openEndpoint}/health",
                                10,
                                10
                            )
                        )
                    }
                }
            }
        }
    }
}
