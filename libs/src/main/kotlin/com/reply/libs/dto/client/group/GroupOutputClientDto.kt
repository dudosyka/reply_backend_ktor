package com.reply.libs.dto.client.group

import com.reply.libs.dto.client.user.UserOutputDto
import kotlinx.serialization.Serializable

@Serializable
data class GroupOutputClientDto (
    val id: Int,
    val name: String,
    val company: Int,
    val users: List<UserOutputDto>
)