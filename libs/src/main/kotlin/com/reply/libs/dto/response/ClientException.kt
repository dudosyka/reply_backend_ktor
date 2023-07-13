package com.reply.libs.dto.response

import kotlinx.serialization.Serializable

@Serializable
open class ClientException (
    val status: Int,
    open val msg: String = ""
)