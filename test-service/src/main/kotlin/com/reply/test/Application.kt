package com.reply.test

import com.reply.libs.config.ApiConfig
import com.reply.libs.database.models.MetricModel
import com.reply.libs.database.models.QuestionModel
import com.reply.libs.database.models.QuestionTypeModel
import com.reply.libs.database.models.TestModel
import com.reply.libs.utils.kodein.bindSingleton
import com.reply.libs.utils.kodein.kodeinApplication
import com.reply.libs.plugins.*
import com.reply.libs.plugins.consul.ConsulServer
import com.reply.libs.utils.database.DatabaseConnector
import com.reply.test.controller.TestController
import com.reply.test.service.QuestionService
import com.reply.test.service.TestService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.logging.*

fun main() = EngineMain.main(Array(0){ "" })

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureValidation()
    responseFilter()
    DatabaseConnector(QuestionTypeModel, MetricModel, TestModel, QuestionModel) {}
    kodeinApplication {
        //Controllers
        bindSingleton { TestController(it) }

        //Services
        bindSingleton { TestService(it) }
        bindSingleton { QuestionService(it) }

        //Logger
        bindSingleton { KtorSimpleLogger("TestService") }
    }
    install(ConsulServer) {
        serviceName = ApiConfig.testServiceName
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
}
