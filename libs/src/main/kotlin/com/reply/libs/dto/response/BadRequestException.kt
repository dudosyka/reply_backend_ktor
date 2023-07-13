package com.reply.libs.dto.response

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class BadRequestException (
    @Transient override val msg: String = "Validation error"
): ClientException(400)