package com.reply.libs.dto.exceptions

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class ForbiddenException(@Transient override val msg: String = "Forbidden"): ClientException(403, "Forbidden")