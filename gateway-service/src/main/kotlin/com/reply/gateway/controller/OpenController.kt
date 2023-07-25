package com.reply.gateway.controller

import com.reply.gateway.consul.UserClient
import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.auth.AuthInputDto
import com.reply.libs.dto.client.auth.AuthOutputDto
import com.reply.libs.dto.client.signup.SignUpInputDto
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.signup.SignUpInputClientDto
import com.reply.libs.utils.kodein.KodeinController
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
        route(ApiConfig.openEndpoint) {
            post("/auth") {
                val result = userClient.withCall(call) {
                    post<AuthInputDto, AuthOutputDto>()!!
                }
                call.respond(result)
            }
            post("/auth/signup") {
                val result = userClient.withCall(call) {
                    post<SignUpInputDto, SuccessOutputDto>("auth/admin/signup")!!
                }
                call.respond(result)
            }
            post("/client/auth/signup") {
                val result = userClient.withCall(call) {
                    post<SignUpInputClientDto, SuccessOutputDto>("auth/client/signup")!!
                }
                call.respond(result)
            }
        }
    }
}