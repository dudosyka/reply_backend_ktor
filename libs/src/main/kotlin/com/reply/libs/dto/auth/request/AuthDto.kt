package com.reply.libs.dto.auth.request

import kotlinx.serialization.Serializable

@Serializable
data class AuthDto (
    val login: String,
    val password: String
)