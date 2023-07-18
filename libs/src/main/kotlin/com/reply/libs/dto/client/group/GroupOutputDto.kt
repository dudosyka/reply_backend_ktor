package com.reply.libs.dto.client.group

import kotlinx.serialization.Serializable

@Serializable
data class GroupOutputDto (
    val id: Int,
    val name: String,
    val company: Int,
    val users: MutableList<Int>
)