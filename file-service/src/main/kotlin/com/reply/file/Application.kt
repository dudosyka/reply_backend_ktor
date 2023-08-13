package com.reply.file

import com.orbitz.consul.model.agent.Registration
import com.reply.file.controller.FileController
import com.reply.file.service.FileService
import com.reply.libs.config.ApiConfig
import com.reply.libs.utils.kodein.bindSingleton
import com.reply.libs.utils.kodein.kodeinApplication
import com.reply.libs.plugins.*
import com.reply.libs.plugins.consul.ConsulServer
import com.reply.libs.utils.consul.FreePortGenerator
import com.reply.libs.utils.consul.health.HealthCheckController
import com.reply.libs.utils.consul.health.ServiceStatus
import com.reply.libs.utils.consul.health.Status
import com.reply.libs.utils.database.DatabaseConnector
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.logging.*

fun main() {
    embeddedServer(Netty, port = FreePortGenerator()) {
        configureSecurity()
        configureHTTP()
        configureMonitoring()
        configureSerialization()
        configureValidation()
        responseFilter()
        kodeinApplication {
            //Controllers
            bindSingleton { FileController(it) }
            bindSingleton { HealthCheckController(it) { ServiceStatus(Status.OK, "Alive") } }

            //Services
            bindSingleton { FileService(it) }

            //Logger
            bindSingleton { KtorSimpleLogger("FileService") }
        }
        DatabaseConnector {}
        environment.monitor.subscribe(ServerReady) { application ->
            application.apply {
                install(ConsulServer) {
                    serviceName = ApiConfig.fileServiceName
                    host = "localhost"
                    port = FreePortGenerator()
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
