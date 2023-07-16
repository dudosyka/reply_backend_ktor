package com.reply.libs.dto.client.auth

import com.reply.libs.dto.client.base.OutputDto
import kotlinx.serialization.Serializable

@Serializable
data class AuthorizedUser(
    val id: Int,
    val login: String,
    val role: Int,
): OutputDto
