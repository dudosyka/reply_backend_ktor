package com.reply.gateway.controller

import com.reply.gateway.consul.TestClient
import com.reply.gateway.consul.UserClient
import com.reply.libs.config.ApiConfig
import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.company.CompanyCreateDto
import com.reply.libs.dto.client.company.CompanyOutputDto
import com.reply.libs.dto.client.company.CompanyUserDto
import com.reply.libs.dto.client.group.GroupCreateClientDto
import com.reply.libs.dto.client.group.GroupOutputClientDto
import com.reply.libs.dto.client.group.GroupOutputDto
import com.reply.libs.dto.client.test.TestCreateDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.dto.internal.exceptions.BadRequestException
import com.reply.libs.utils.consul.EmptyBody
import com.reply.libs.utils.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

//Controller for admin-part of application
class AdminController(override val di: DI) : KodeinController() {
    private val testClient: TestClient by instance()
    private val userClient: UserClient by instance()
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.ADMIN.toString()) {
            route(ApiConfig.adminEndpoint) {
                // Test-service routing crud endpoints
                route("test") {
                    get {
                        val result = testClient.withCall(call) {
                            get<MutableList<TestOutputDto>>()!!
                        }
                        call.respond(result)
                    }
                    get("{id}") {
                        val result = testClient.withCall(call) {
                            call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                            get<TestOutputDto>()!!
                        }
                        call.respond(result)
                    }
                    post {
                        val result = testClient.withCall(call) {
                            post<TestCreateDto, TestOutputDto>()!!
                        }
                        call.respond(result)
                    }
                    delete("{id}") {
                        val result = testClient.withCall(call) {
                            call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                            delete<EmptyBody, SuccessOutputDto>(EmptyBody)!!
                        }
                        call.respond(result)
                    }
                    patch("{id}") {
                        val result = testClient.withCall(call) {
                            call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                            patch<TestCreateDto, SuccessOutputDto>()!!
                        }
                        call.respond(result)
                    }
                }

                // Company-service routing
                route("company") {
                    route("users") {
                        get {
                            val result = userClient.withCall(call) {
                                get<List<CompanyUserDto>>()!!
                            }
                            call.respond(result)
                        }
                    }
                    route("groups") {
                        get {
                            val result = userClient.withCall(call) {
                                get<List<GroupOutputDto>>()!!
                            }
                            call.respond(result)
                        }
                    }
                    patch {
                        val result = userClient.withCall(call) {
                            patch<CompanyCreateDto, CompanyOutputDto>()!!
                        }
                        call.respond(result)
                    }
                }

                route("group") {
                    get {
                        val result = userClient.withCall(call) {
                            get<List<GroupOutputDto>>()!!
                        }
                        call.respond(result)
                    }
                    get("{id}") {
                        val result = userClient.withCall(call) {
                            call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                            get<GroupOutputClientDto>()!!
                        }
                        call.respond(result)
                    }
                    post {
                        val result = userClient.withCall(call) {
                            post<GroupCreateClientDto, GroupOutputClientDto>()!!
                        }
                        call.respond(result)
                    }
                    delete("{id}") {
                        val result = userClient.withCall(call) {
                            call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                            delete<EmptyBody, SuccessOutputDto>(EmptyBody)!!
                        }
                        call.respond(result)
                    }
                    patch("{id}") {
                        val result = userClient.withCall(call) {
                            call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                            patch<GroupCreateClientDto, GroupOutputClientDto>()!!
                        }
                        call.respond(result)
                    }
                }
            }
        }
    }
}