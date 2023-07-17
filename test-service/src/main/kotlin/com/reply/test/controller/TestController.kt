package com.reply.test.controller

import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.test.TestCreateDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.dto.internal.exceptions.BadRequestException
import com.reply.libs.dto.internal.exceptions.InternalServerError
import com.reply.libs.utils.kodein.KodeinController
import com.reply.test.service.TestService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

class TestController(override val di: DI) : KodeinController() {
    private val testService: TestService by instance()
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.ADMIN.toString()) {

            //CRUD Endpoints
            route("test") {
                get {
                    call.respond<MutableList<TestOutputDto>>(testService.getAll(getAuthorized(call.principal<JWTPrincipal>()!!)))
                }
                get("{id}") {
                    val testId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                    call.respond<TestOutputDto>(testService.getOne(testId))
                }
                post {
                    val createDto = call.receive<TestCreateDto>()
                    call.respond<TestOutputDto>(testService.create(createDto, getAuthorized(call.principal<JWTPrincipal>()!!)))
                }
                delete("{id}") {
                    val testId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                    testService.delete(testId, getAuthorized(call.principal<JWTPrincipal>()!!))
                    call.respond<SuccessOutputDto>(SuccessOutputDto("success", "Test successfully removed"))
                }
                patch("{id}") {
                    val testId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                    val updateDto = call.receive<TestCreateDto>()
                    val result = testService.patch(updateDto, testId, getAuthorized(call.principal<JWTPrincipal>()!!))
                    if (result)
                        call.respond<SuccessOutputDto>(SuccessOutputDto("success", "Test successfully updated"))
                    else
                        throw InternalServerError("Failed to update test with id = $testId")
                }
            }
        }
    }
}