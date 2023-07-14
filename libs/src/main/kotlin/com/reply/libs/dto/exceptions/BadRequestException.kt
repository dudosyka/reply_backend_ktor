package com.reply.libs.dto.exceptions

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class BadRequestException (
    @Transient override val msg: String = "Validation error"
): ClientException(400, "Bad request")