package com.reply.gateway.controller

import com.reply.libs.config.ApiConfig
import com.reply.libs.config.RBACConfig
import com.reply.libs.utils.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI

//Controller for client-part of application
class ClientController(override val di: DI) : KodeinController() {
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.CLIENT.toString()) {
            route(ApiConfig.clientEndpoint) {
                get {
                    call.respondText("Client part")
                }
            }
        }
    }
}