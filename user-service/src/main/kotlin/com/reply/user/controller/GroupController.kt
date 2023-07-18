package com.reply.user.controller

import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.client.group.GroupCreateClientDto
import com.reply.libs.dto.client.group.GroupOutputClientDto
import com.reply.libs.dto.client.group.GroupOutputDto
import com.reply.libs.dto.internal.exceptions.BadRequestException
import com.reply.libs.utils.kodein.KodeinController
import com.reply.user.service.CompanyService
import com.reply.user.service.GroupService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

class GroupController(override val di: DI) : KodeinController() {
    private val groupService: GroupService by instance()
    private val companyService: CompanyService by instance()
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.ADMIN.toString()) {
            route("group") {
                get {
                    val authorizedUser = getAuthorized(call)
                    call.respond<List<GroupOutputDto>>(groupService.getAllForAuthorized(authorizedUser))
                }
                get("{id}") {
                    val authorizedUser = getAuthorized(call)
                    val groupId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                    call.respond<GroupOutputClientDto>(groupService.getOne(groupId, authorizedUser))
                }
                post {
                    val authorizedUser = getAuthorized(call)
                    val groupCreateDto = call.receive<GroupCreateClientDto>()
                    call.respond<GroupOutputDto>(groupService.create(groupCreateDto, authorizedUser))
                }
            }
        }
    }
}