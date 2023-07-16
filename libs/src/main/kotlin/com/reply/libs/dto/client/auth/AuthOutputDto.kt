package com.reply.libs.dto.client.auth

import com.reply.libs.dto.client.base.OutputDto
import kotlinx.serialization.Serializable

@Serializable
data class AuthOutputDto(
    val token: String
): OutputDto