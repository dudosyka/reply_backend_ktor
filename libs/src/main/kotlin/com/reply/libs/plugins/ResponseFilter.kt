package com.reply.libs.plugins

import com.reply.libs.dto.exceptions.BadRequestException
import com.reply.libs.dto.exceptions.ClientException
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.logging.*

fun Application.responseFilter() {
    val logger = KtorSimpleLogger("com.reply.ResponseFilter")
    install(StatusPages) {
        exception<RequestValidationException> {
            call, requestValidationException -> run {
                logger.info("Request ${call.request.path()} was failed cause to $requestValidationException")
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BadRequestException(requestValidationException.message ?: "Validation error")
                )
            }
        }
        exception<ClientException> {
            call, requestValidationException -> run {
                logger.info("Request ${call.request.path()} was failed cause to $requestValidationException")
                call.respond(
                    status = HttpStatusCode(requestValidationException.status, requestValidationException.statusDescription),
                    message = requestValidationException
                )
            }
        }
    }
}