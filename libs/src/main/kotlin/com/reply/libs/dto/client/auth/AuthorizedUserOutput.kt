package com.reply.libs.dto.client.auth

import kotlinx.serialization.Serializable

@Serializable
data class AuthorizedUserOutput(
    val id: Int,
    val login: String,
    val role: Int,
    val companyId: Int
)
