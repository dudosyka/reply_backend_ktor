package com.reply.block

import com.reply.block.controller.BlockController
import com.reply.block.service.BlockService
import com.reply.gateway.consul.TestClient
import com.reply.libs.utils.kodein.bindSingleton
import com.reply.libs.utils.kodein.kodeinApplication
import com.reply.libs.plugins.*
import io.ktor.server.application.*
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
}
