package com.reply.libs.dto.client.user

import kotlinx.serialization.Serializable

@Serializable
data class UserUpdateDto (
    val login: String,
    val avatar: Int?,
    val password: String,
    val fullname: String,
    val phone: String,
    val email: String,
)