package com.reply.libs.dto.client.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthInputDto (
    val login: String,
    val password: String
)