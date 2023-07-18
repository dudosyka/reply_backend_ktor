package com.reply.libs.dto.client.test

import kotlinx.serialization.Serializable

@Serializable
data class TestOutputDto(
    val id: Int,
    val title: String,
    val type: Int,
    val company: Int?,
    val formula: String,
    val author: Int?,
    val metric: Int
)