package com.reply.gateway.controller

import com.reply.gateway.consul.TestClient
import com.reply.libs.config.ApiConfig
import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.test.TestCreateDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.dto.client.test.TestUpdateDto
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
    val testClient: TestClient by instance()
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.ADMIN.toString()) {
            route(ApiConfig().adminEndpoint) {
                get {
                    call.respondText("Admin part")
                }
                // Test-entity crud endpoints
                route("test") {
                    get {
                        val result = testClient.withCall(call) {
                            get<MutableList<TestOutputDto>>("test")!!
                        }
                        call.respond(result)
                    }
                    get("{id}") {
                        val result = testClient.withCall(call) {
                            val testId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                            get<TestOutputDto>("test/${testId}")!!
                        }
                        call.respond(result)
                    }
                    post {
                        val result = testClient.withCall(call) {
                            post<TestCreateDto, TestOutputDto>("test")!!
                        }
                        call.respond(result)
                    }
                    delete("{id}") {
                        val result = testClient.withCall(call) {
                            val testId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                            delete<EmptyBody, SuccessOutputDto>("test/$testId", EmptyBody)!!
                        }
                        call.respond(result)
                    }
                    patch("{id}") {
                        val result = testClient.withCall(call) {
                            val testId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                            patch<TestUpdateDto, SuccessOutputDto>("test/$testId")!!
                        }
                        call.respond(result)
                    }
                }
            }
        }
    }
}