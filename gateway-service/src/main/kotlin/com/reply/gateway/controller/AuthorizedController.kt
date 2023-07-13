package com.reply.gateway.controller

import com.reply.libs.config.ApiConfig
import com.reply.libs.config.RBACConfig
import com.reply.libs.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI

//Controller for authorized-part of application (when it must be opened both for client and admin users)
class AuthorizedController(override val di: DI) : KodeinController() {
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.AUTHORIZED.toString()) {
            route(ApiConfig().authorizedEndpoint) {
                get {
                    call.respondText("Authorized part")
                }
            }
        }
    }
}