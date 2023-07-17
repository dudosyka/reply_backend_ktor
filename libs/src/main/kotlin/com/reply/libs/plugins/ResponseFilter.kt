package com.reply.libs.plugins

import com.reply.libs.dto.internal.exceptions.BadRequestException
import com.reply.libs.dto.internal.exceptions.ClientException
import com.reply.libs.dto.internal.exceptions.ModelNotFound
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.util.*
import io.ktor.util.logging.*
import org.jetbrains.exposed.dao.exceptions.EntityNotFoundException
import org.jetbrains.exposed.exceptions.ExposedSQLException
import io.ktor.server.plugins.BadRequestException as BadRequestKtorException

@OptIn(InternalAPI::class)
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
        exception<BadRequestKtorException> {
            call, requestValidationException -> run {
                logger.info("Request ${call.request.path()} was failed cause to $requestValidationException")
                call.respond(
                    status = HttpStatusCode.BadRequest,
                    message = BadRequestException(requestValidationException.rootCause?.message ?: "Validation error")
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
        exception<EntityNotFoundException> {
            call, requestValidationException -> run {
                logger.info("Request ${call.request.path()} was failed cause to $requestValidationException")
                call.respond(
                    status = HttpStatusCode(404, "Not found"),
                    message = ModelNotFound(requestValidationException.message ?: "Entity not found")
                )
            }
        }
        exception<ExposedSQLException> {
            call, requestValidationException -> run {
                logger.info("Request ${call.request.path()} was failed cause to $requestValidationException")
                logger.info("Stacktrace => ${requestValidationException.stackTraceToString()}")
                call.respond(
                    status = HttpStatusCode(500, "Internal server error"),
                    message = ClientException(500, "Internal server error", requestValidationException.message ?: "error")
                )
            }
        }
        exception<Exception> {
            call, requestValidationException -> run {
                logger.info("Request ${call.request.path()} was failed cause to $requestValidationException")
                logger.info("Stacktrace => ${requestValidationException.stackTraceToString()}")
                call.respond(
                    status = HttpStatusCode(500, "Internal server error"),
                    message = ClientException(500, "Internal server error", requestValidationException.message ?: "error")
                )
            }
        }
    }
}