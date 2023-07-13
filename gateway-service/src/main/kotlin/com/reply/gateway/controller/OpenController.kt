package com.reply.gateway.controller

import com.reply.gateway.service.UserClient
import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.auth.request.AuthDto
import com.reply.libs.dto.auth.response.AuthOutputDto
import com.reply.libs.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

//Controller for open-part of application
class OpenController(override val di: DI) : KodeinController() {
    private val userClient: UserClient by instance()

    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        route(ApiConfig().openEndpoint) {
            get {
                val result = userClient.get<String>("test", call)
                println(result)
                call.respondText("Open part, result: $result")
            }
            post("/auth") {
                val result = userClient.post<AuthDto, AuthOutputDto>("auth", call)
                call.respond(result)
            }
        }
    }
}