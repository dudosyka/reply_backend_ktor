package com.reply.libs.dto.client.group_users

import kotlinx.serialization.Serializable

@Serializable
data class GroupUsersOutputDto (
    val id: Int,
    val user: Int,
    val group: Int
)