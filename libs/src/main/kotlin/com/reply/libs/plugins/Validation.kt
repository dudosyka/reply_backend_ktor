package com.reply.libs.plugins

import com.reply.libs.dto.client.auth.AuthInputDto
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*


fun Application.configureValidation() {
    install(RequestValidation) {
        validate<AuthInputDto> {
            if (it.login.length < 2)
                ValidationResult.Invalid("Login must be longer than 10 symbols")
            else
                ValidationResult.Valid
        }
    }
}