package com.reply.test

import com.orbitz.consul.model.agent.Registration
import com.reply.libs.config.ApiConfig
import com.reply.libs.database.models.MetricModel
import com.reply.libs.database.models.QuestionModel
import com.reply.libs.database.models.QuestionTypeModel
import com.reply.libs.database.models.TestModel
import com.reply.libs.utils.kodein.bindSingleton
import com.reply.libs.utils.kodein.kodeinApplication
import com.reply.libs.plugins.*
import com.reply.libs.plugins.consul.ConsulServer
import com.reply.libs.utils.consul.FreePortGenerator
import com.reply.libs.utils.consul.health.HealthCheckController
import com.reply.libs.utils.consul.health.ServiceStatus
import com.reply.libs.utils.consul.health.Status
import com.reply.libs.utils.database.DatabaseConnector
import com.reply.test.consul.UserClient
import com.reply.test.controller.BlockController
import com.reply.test.controller.TestController
import com.reply.test.service.BlockService
import com.reply.test.service.QuestionService
import com.reply.test.service.TestService
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
        DatabaseConnector(QuestionTypeModel, MetricModel, TestModel, QuestionModel) {}
        kodeinApplication {
            //Consul
            bindSingleton { UserClient(it) }

            //Controllers
            bindSingleton { TestController(it) }
            bindSingleton { BlockController(it) }
            bindSingleton { HealthCheckController(it) { ServiceStatus(Status.OK, "Alive") } }

            //Services
            bindSingleton { TestService(it) }
            bindSingleton { BlockService(it) }
            bindSingleton { QuestionService(it) }

            //Logger
            bindSingleton { KtorSimpleLogger("TestService") }
        }
        environment.monitor.subscribe(ServerReady) { application ->
            application.apply {
                install(ConsulServer) {
                    serviceName = ApiConfig.testServiceName
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