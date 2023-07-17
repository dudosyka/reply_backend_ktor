package com.reply.libs.dto.client.question

import kotlinx.serialization.Serializable

@Serializable
data class QuestionTypeOutputDto (
    val id: Int,
    val name: String
)