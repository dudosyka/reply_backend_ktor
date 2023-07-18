package com.reply.user.controller

import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.client.company.CompanyUserDto
import com.reply.libs.dto.client.group.GroupOutputDto
import com.reply.libs.utils.kodein.KodeinController
import com.reply.user.service.CompanyService
import com.reply.user.service.GroupService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

class CompanyController(override val di: DI) : KodeinController() {
    private val companyService: CompanyService by instance()
    private val groupService: GroupService by instance()
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */

    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.ADMIN.toString()) {
            route("company") {
                route("users") {
                    get {
                        val authorizedUser = getAuthorized(call)
                        call.respond<List<CompanyUserDto>>(companyService.getUsers(authorizedUser.companyId, authorizedUser))
                    }
                }
                route("groups") {
                    get {
                        val authorizedUser = getAuthorized(call)
                        call.respond<List<GroupOutputDto>>(companyService.getGroups(authorizedUser.companyId, authorizedUser))
                    }
                }
            }
        }
    }
}