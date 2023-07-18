package com.reply.libs.dto.client.question

import kotlinx.serialization.Serializable

@Serializable
data class QuestionOutputDto (
    val id: Int,
    val title: String,
    val type: Int,
    val test: Int,
    val relative_id: Int,
    val value: MutableList<QuestionValueDto>,
    val coins: Int,
    val picture: Int?
)