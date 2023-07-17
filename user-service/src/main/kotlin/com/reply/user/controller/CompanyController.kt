package com.reply.user.controller

import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.client.user.UserOutputDto
import com.reply.libs.utils.kodein.KodeinController
import com.reply.user.service.CompanyService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

class CompanyController(override val di: DI) : KodeinController() {
    private val companyService: CompanyService by instance()
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.ADMIN.toString()) {
            route("company") {
                route("users") {
                    get {
                        val authorizedUser = getAuthorized(call)
                        call.respond<List<UserOutputDto>>(companyService.getUsers(authorizedUser.companyId, authorizedUser))
                    }
                }
            }
        }
    }
}