package com.reply.user.controller

import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.group.GroupCreateClientDto
import com.reply.libs.dto.client.group.GroupOutputClientDto
import com.reply.libs.dto.client.group.GroupOutputDto
import com.reply.libs.dto.internal.exceptions.BadRequestException
import com.reply.libs.utils.kodein.KodeinController
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
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.ADMIN.toString()) {
            route("group") {
                //CRUD endpoints
                get {
                    val authorizedUser = getAuthorized(call)
                    call.respond<List<GroupOutputDto>>(groupService.getAllForAuthorized(authorizedUser))
                }
                post {
                    val authorizedUser = getAuthorized(call)
                    val groupCreateDto = call.receive<GroupCreateClientDto>()
                    call.respond<GroupOutputClientDto>(groupService.create(groupCreateDto, authorizedUser))
                }
                route("{id}") {
                    get {
                        val authorizedUser = getAuthorized(call)
                        val groupId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                        call.respond<GroupOutputClientDto>(groupService.get(groupId, authorizedUser))
                    }
                    patch {
                        val authorizedUser = getAuthorized(call)
                        val groupId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                        val groupUpdateDto = call.receive<GroupCreateClientDto>()
                        call.respond<GroupOutputClientDto>(groupService.update(groupId, groupUpdateDto, authorizedUser))
                    }
                    delete {
                        val authorizedUser = getAuthorized(call)
                        val groupId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                        call.respond<SuccessOutputDto>(groupService.delete(groupId, authorizedUser))
                    }
                    route("user") {
                        route("{userId}") {
                            patch("append") {
                                val authorizedUser = getAuthorized(call)
                                val groupId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                                val userId = call.parameters["userId"]?.toIntOrNull() ?: throw BadRequestException()
                                call.respond<SuccessOutputDto>(groupService.appendUser(groupId, userId, authorizedUser))
                            }
                            patch("remove") {
                                val authorizedUser = getAuthorized(call)
                                val groupId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                                val userId = call.parameters["userId"]?.toIntOrNull() ?: throw BadRequestException()
                                call.respond<SuccessOutputDto>(groupService.removeUser(groupId, userId, authorizedUser))
                            }
                        }
                    }
                }
            }
        }
    }
}