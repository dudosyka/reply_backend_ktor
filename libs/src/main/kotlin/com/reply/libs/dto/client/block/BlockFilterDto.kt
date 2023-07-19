package com.reply.libs.dto.client.block

import kotlinx.serialization.Serializable

@Serializable
data class BlockFilterDto(
    val id : List<Int>,
    val exclude_test : Int?,
    val company_id : Int?
)