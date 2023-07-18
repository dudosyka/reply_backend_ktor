package com.reply.libs.dto.client.group

import kotlinx.serialization.Serializable

@Serializable
data class GroupCreateDto (
    val name: String,
    val company: Int?,
    val users: List<Int>?
)