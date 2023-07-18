package com.reply.libs.plugins

import com.reply.libs.dto.client.auth.AuthInputDto
import com.reply.libs.dto.client.block.BlockCreateDto
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
        validate<BlockCreateDto>{
            if (it.name.length > 255)
                ValidationResult.Invalid("Name must be shorter than 255 symbols")
            else
                ValidationResult.Valid
            if (it.description.length > 255)
                ValidationResult.Invalid("Description must be shorter than 255 symbols")
            else
                ValidationResult.Valid
        }
    }
}