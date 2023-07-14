package com.reply.libs.dto.auth.response

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizedUser(
    val id: Int,
    val login: String,
    val role: Int,
)
