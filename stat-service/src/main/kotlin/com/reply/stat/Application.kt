package com.reply.stat

import com.orbitz.consul.model.agent.Registration
import com.reply.libs.config.ApiConfig
import com.reply.libs.database.models.result.BlockResultModel
import com.reply.libs.database.models.result.BlockTestResultModel
import com.reply.libs.database.models.result.TestResultModel
import com.reply.libs.utils.kodein.bindSingleton
import com.reply.libs.utils.kodein.kodeinApplication
import com.reply.libs.plugins.*
import com.reply.libs.plugins.consul.ConsulServer
import com.reply.libs.utils.consul.FreePortGenerator
import com.reply.libs.utils.consul.health.HealthCheckController
import com.reply.libs.utils.consul.health.ServiceStatus
import com.reply.libs.utils.consul.health.Status
import com.reply.libs.utils.database.DatabaseConnector
import com.reply.stat.controller.ResultController
import com.reply.stat.service.ResultService
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
        DatabaseConnector(BlockResultModel, BlockTestResultModel, TestResultModel) {}
        kodeinApplication {
            //Controllers
            bindSingleton { ResultController(it) }
            bindSingleton { HealthCheckController(it) { ServiceStatus(Status.OK, "Alive") } }

            //Services
            bindSingleton { ResultService(it) }

            bindSingleton { KtorSimpleLogger("StatService") }
        }
        environment.monitor.subscribe(ServerReady) { application ->
            application.apply {
                install(ConsulServer) {
                    serviceName = ApiConfig.statServiceName
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
    }.start(wait = true)
}
