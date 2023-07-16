package com.reply.libs.dto.internal.exceptions

import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
class DuplicateEntryException(@Transient override val msg: String = ""): ClientException(409, "Duplicate entry")