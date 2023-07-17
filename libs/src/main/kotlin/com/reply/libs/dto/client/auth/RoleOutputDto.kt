package com.reply.libs.dto.client.auth

import kotlinx.serialization.Serializable

@Serializable
data class RoleOutputDto (
    val id: Int,
    val name: String,
    val description: String?
)