package com.reply.libs.dto.exceptions

import kotlinx.serialization.Serializable

@Serializable
open class ClientException (
    val status: Int,
    val statusDescription: String,
    open val msg: String = ""
): Exception()