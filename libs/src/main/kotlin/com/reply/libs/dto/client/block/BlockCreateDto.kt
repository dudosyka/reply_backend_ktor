package com.reply.libs.dto.client.block

import kotlinx.serialization.Serializable

@Serializable
data class BlockCreateDto(
    val name :  String,
    val description: String,
    val time : Int,
    val tests : List<Int>
)