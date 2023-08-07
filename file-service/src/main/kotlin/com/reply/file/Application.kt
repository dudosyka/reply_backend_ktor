package com.reply.file

import com.reply.file.controller.FileController
import com.reply.file.service.FileService
import com.reply.libs.config.ApiConfig
import com.reply.libs.utils.kodein.bindSingleton
import com.reply.libs.utils.kodein.kodeinApplication
import com.reply.libs.plugins.*
import com.reply.libs.plugins.consul.ConsulServer
import com.reply.libs.utils.database.DatabaseConnector
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
    kodeinApplication {
        //Controllers
        bindSingleton { FileController(it) }

        //Services
        bindSingleton { FileService(it) }

        //Logger
        bindSingleton { KtorSimpleLogger("FileService") }
    }
    DatabaseConnector {}
    install(ConsulServer) {
        serviceName = ApiConfig.fileServiceName
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
