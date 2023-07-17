package com.reply.libs.dto.client.question

import kotlinx.serialization.Serializable

@Serializable
data class QuestionValueDto (
    val id: Int,
    val title: String,
    val value: Int
) {
}