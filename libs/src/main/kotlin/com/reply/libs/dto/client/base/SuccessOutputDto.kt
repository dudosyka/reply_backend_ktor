package com.reply.libs.dto.client.base

import kotlinx.serialization.Serializable

@Serializable
data class SuccessOutputDto (
    val status: String = "Success",
    val msg: String
)