package com.reply.user.controller

import com.reply.libs.config.RBACConfig
import com.reply.libs.utils.kodein.KodeinController
import com.reply.libs.dto.client.auth.AuthorizedUserOutput
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI

class CheckTokenController(override val di: DI) : KodeinController() {
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.AUTHORIZED.toString()) {
            get("/authorized") {
                val user = getAuthorized(call)
                call.respond<AuthorizedUserOutput>(
                    AuthorizedUserOutput(
                        user.id,
                        user.login,
                        user.role,
                        user.companyId
                    )
                )
            }
        }
    }
}