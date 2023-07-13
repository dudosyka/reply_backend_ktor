package com.reply.libs.plugins

import com.reply.libs.dto.auth.request.AuthDto
import io.ktor.server.application.*
import io.ktor.server.plugins.requestvalidation.*


fun Application.configureValidation() {
    install(RequestValidation) {
        validate<AuthDto> {
            if (it.login.length < 2)
                ValidationResult.Invalid("Login must be longer than 10 symbols")
            else
                ValidationResult.Valid
        }
    }
}