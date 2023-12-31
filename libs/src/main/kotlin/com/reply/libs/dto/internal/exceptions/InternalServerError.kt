package com.reply.libs.dto.internal.exceptions

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class InternalServerError(
    @Transient override val msg: String = ""
): ClientException(500, "Internal server error")