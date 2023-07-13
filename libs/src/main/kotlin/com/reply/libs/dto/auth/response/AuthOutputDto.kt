package com.reply.libs.dto.auth.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthOutputDto(
    val token: String
)