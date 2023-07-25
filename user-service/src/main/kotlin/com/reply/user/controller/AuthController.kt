package com.reply.user.controller

import com.reply.libs.dto.client.auth.AuthInputDto
import com.reply.libs.dto.client.auth.AuthOutputDto
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.signup.SignUpInputClientDto
import com.reply.libs.dto.client.block.BlockTokenDto
import com.reply.libs.utils.kodein.KodeinController
import com.reply.libs.dto.client.signup.SignUpInputDto
import com.reply.user.service.AuthService
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import org.kodein.di.DI
import org.kodein.di.instance

class AuthController(override val di: DI) : KodeinController() {
    private val authService: AuthService by instance()
    private val logger: Logger by instance()

    override fun Routing.registerRoutes() {
        route("/auth") {
            post {
                val data = call.receive<AuthInputDto>()
                logger.info(data.toString())
                call.respond<AuthOutputDto>(authService.authUser(data))
            }

            post("/admin/signup") {
                val data = call.receive<SignUpInputDto>()
                call.respond<SuccessOutputDto>(authService.signUpAdmin(data, call))
            }

            post("/client/signup") {
                val data = call.receive<SignUpInputClientDto>()
                call.respond<SuccessOutputDto>(authService.signUpClient(data, call))
            }
        }
        route("token"){
            post("block") {
                val data = call.receive<BlockTokenDto>()
                call.respond(authService.getToken(data))
            }
        }
    }
}