package com.reply.libs.dto.internal.exceptions

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class ModelNotFound(@Transient override val msg: String = "Entity not found"): ClientException(404, "Not found")