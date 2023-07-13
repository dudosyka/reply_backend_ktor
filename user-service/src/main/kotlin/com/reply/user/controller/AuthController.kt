package com.reply.user.controller

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.reply.libs.config.JwtConfig
import com.reply.user.service.AuthService
import com.reply.libs.dto.auth.request.AuthDto
import com.reply.libs.dto.auth.response.AuthorizedUser
import com.reply.libs.kodein.KodeinController
import com.reply.libs.plugins.createToken
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*
import org.kodein.di.DI
import org.kodein.di.instance
import java.util.*

class AuthController(override val di: DI) : KodeinController() {
    private val authService: AuthService by instance()
    private val logger: Logger by instance()

    override fun Routing.registerRoutes() {
        get("test") {
            println(call.request.local.localPort)
        }

        post("/login") {
            val data = call.receive<AuthDto>()
            logger.info(data.toString())
            call.respond(hashMapOf("token" to authService.authUser(data)))
        }

        authenticate("Authorized") {
            get("/authorized") {
                val principal = call.principal<JWTPrincipal>()!!
                call.respond<AuthorizedUser>(AuthorizedUser(principal.getClaim("login", String::class)!!, principal.getClaim("role", Int::class)!!))
            }
        }

        authenticate("Admin role") {
            get("/authorized/admin") {
                val principal = call.principal<JWTPrincipal>()!!
                call.respond<AuthorizedUser>(AuthorizedUser(principal.getClaim("login", String::class)!!, principal.getClaim("role", Int::class)!!))
            }
        }

        authenticate("User role") {
            get("/authorized/user") {
                val principal = call.principal<JWTPrincipal>()!!
                call.respond<AuthorizedUser>(AuthorizedUser(principal.getClaim("login", String::class)!!, principal.getClaim("role", Int::class)!!))
            }
        }
    }

}