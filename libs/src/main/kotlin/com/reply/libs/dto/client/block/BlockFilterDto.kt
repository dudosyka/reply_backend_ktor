package com.reply.libs.dto.client.block

data class BlockFilterDto(
    val id : List<Int>,
    val exclude_test : Int?,
    val company_id : Int?
)