package com.reply.gateway.controller

import com.reply.libs.config.ApiConfig
import com.reply.libs.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI

//Controller for open-part of application
class OpenController(override val di: DI) : KodeinController() {
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        route(ApiConfig().openEndpoint) {
            get {
                call.respondText("Open part")
            }
            get ("user/health") {
                call.respondText("User service health check")
            }
        }
    }
}