package com.reply.libs.utils.consul.health

import com.reply.libs.config.ApiConfig
import com.reply.libs.utils.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI

class HealthCheckController(override val di: DI, val checker: () -> ServiceStatus) : KodeinController() {
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        route(ApiConfig.openEndpoint) {
            get("health") {
                call.respond(checker())
            }
        }
    }

}