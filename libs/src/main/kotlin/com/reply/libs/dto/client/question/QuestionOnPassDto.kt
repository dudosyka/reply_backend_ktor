package com.reply.libs.dto.client.question

import kotlinx.serialization.Serializable

@Serializable
data class QuestionOnPassDto (
    val id: Int,
    val title: String,
    val type: Int,
    val relative_id: Int,
    val value: List<QuestionValueDto>,
    val coins: Int,
    val picture: Int?
)