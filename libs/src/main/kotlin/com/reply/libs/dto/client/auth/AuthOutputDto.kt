package com.reply.libs.dto.client.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthOutputDto(
    val token: String
)