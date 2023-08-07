package com.reply.gateway.controller

import com.reply.gateway.consul.UserClient
import com.reply.libs.config.ApiConfig
import com.reply.libs.config.RBACConfig
import com.reply.libs.consul.FileServiceClient
import com.reply.libs.dto.client.auth.AuthorizedUserOutput
import com.reply.libs.dto.client.user.UserOutputDto
import com.reply.libs.dto.client.user.UserUpdateDto
import com.reply.libs.utils.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

//Controller for authorized-part of application (when it must be opened both for client and admin users)
class AuthorizedController(override val di: DI) : KodeinController() {
    private val userClient: UserClient by instance()
    private val fileClient: FileServiceClient by instance()

    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.AUTHORIZED.toString()) {
            route(ApiConfig.authorizedEndpoint) {
                get {
                    val result = userClient.withCall(call) {
                        get<AuthorizedUserOutput>("authorized")
                    }
                    call.respond(result!!)
                }
                route("user") {
                    patch("{id}") {
                        val result = userClient.withCall(call) {
                            patch<UserUpdateDto, UserOutputDto>()
                        }

                        call.respond(result!!)
                    }
                }
                get ("file/{fileId}") {
                    val file = fileClient.withCall(call) {
                        getFile()
                    }
                    call.respondBytes(file)
                }
            }
        }
    }
}