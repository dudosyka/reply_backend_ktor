package com.reply.block

import com.reply.block.controller.BlockController
import com.reply.block.service.BlockService
import com.reply.gateway.consul.TestClient
import com.reply.libs.config.ApiConfig
import com.reply.libs.database.models.BlockModel
import com.reply.libs.database.models.BlockTestsModel
import com.reply.libs.database.models.TestModel
import com.reply.libs.utils.kodein.bindSingleton
import com.reply.libs.utils.kodein.kodeinApplication
import com.reply.libs.plugins.*
import com.reply.libs.plugins.consul.ConsulServer
import com.reply.libs.utils.database.DatabaseConnector
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.logging.*

fun main() = EngineMain.main(Array<String>(0){ "" })

fun Application.module() {
    configureSecurity()
    configureHTTP()
    configureMonitoring()
    configureSerialization()
    configureValidation()
    responseFilter()
    DatabaseConnector(BlockModel, TestModel, BlockTestsModel) {}
    kodeinApplication {
        //Consul
        bindSingleton { TestClient(it) }
        //Services
        bindSingleton { BlockService(it) }
        //Controllers
        bindSingleton { BlockController(it) }
        //Logger
        bindSingleton { KtorSimpleLogger("BlockService") }
    }
    install(ConsulServer) {
        serviceName = ApiConfig.blockServiceName
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
