package com.reply.gateway

import com.reply.gateway.controller.AdminController
import com.reply.gateway.controller.AuthorizedController
import com.reply.gateway.controller.ClientController
import com.reply.gateway.controller.OpenController
import com.reply.libs.kodein.bindSingleton
import com.reply.libs.kodein.kodeinApplication
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
        bindSingleton { KtorSimpleLogger("GatewayService") }
        bindSingleton { AdminController(it) }
        bindSingleton { AuthorizedController(it) }
        bindSingleton { ClientController(it) }
        bindSingleton { OpenController(it) }
    }
}
