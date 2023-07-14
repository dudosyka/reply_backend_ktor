package com.reply.user.controller

import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.auth.request.AuthDto
import com.reply.libs.dto.auth.response.AuthorizedUser
import com.reply.libs.config.kodein.KodeinController
import com.reply.user.service.AuthService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
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
        get("test") {
            call.respondText("test")
        }

        post("/auth") {
            val data = call.receive<AuthDto>()
            logger.info(data.toString())
            call.respond(authService.authUser(data))
        }

        authenticate(RBACConfig.AUTHORIZED.toString()) {
            get("/authorized") {
                val user = getAuthorized(call.principal<JWTPrincipal>()!!)
                call.respond<AuthorizedUser>(
                    AuthorizedUser(
                        user.id,
                        user.login,
                        user.role
                    )
                )
            }
        }

        authenticate(RBACConfig.ADMIN.toString()) {
            get("/authorized/admin") {
                val user = getAuthorized(call.principal<JWTPrincipal>()!!)
                call.respond<AuthorizedUser>(
                    AuthorizedUser(
                        user.id,
                        user.login,
                        user.role
                    )
                )
            }
        }

        authenticate(RBACConfig.CLIENT.toString()) {
            get("/authorized/user") {
                val user = getAuthorized(call.principal<JWTPrincipal>()!!)
                call.respond<AuthorizedUser>(
                    AuthorizedUser(
                        user.id,
                        user.login,
                        user.role
                    )
                )
            }
        }
    }

}