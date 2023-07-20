package com.reply.libs.dto.client.block

import kotlinx.serialization.Serializable

@Serializable
data class BlockTokenDto(
    val blockId : Int,
    val week : Int,
    val userId : Int,
)