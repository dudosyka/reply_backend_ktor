package com.reply.libs.dto.client.group

import kotlinx.serialization.Serializable

@Serializable
data class GroupCreateClientDto (
    val name: String,
    val users: List<Int>
)