package com.reply.user.controller

import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.client.user.UserCheckExistingOutputDto
import com.reply.libs.dto.client.user.UserCheckExistsInputDto
import com.reply.libs.dto.client.user.UserOutputDto
import com.reply.libs.dto.client.user.UserUpdateDto
import com.reply.libs.dto.internal.exceptions.BadRequestException
import com.reply.libs.utils.kodein.KodeinController
import com.reply.user.service.UserService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

class UserController(override val di: DI) : KodeinController() {
    private val userService: UserService by instance()
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.AUTHORIZED.toString()) {
            route("user") {
                get("{id}") {
                    val userId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                    call.respond<UserOutputDto>(userService.getById(userId))
                }

                patch("{id}") {
                    val userId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                    val userData = call.receive<UserUpdateDto>()

                    call.respond<UserOutputDto>(userService.patch(userData, userId, getAuthorized(call), call))
                }
                get("check-exists") {
                    val dataOnCheck = call.receive<UserCheckExistsInputDto>()

                    call.respond<UserCheckExistingOutputDto>(userService.checkExisting(dataOnCheck))
                }
            }
        }
    }

}