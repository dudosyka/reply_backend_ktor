package com.reply.libs.dto.client.test

import com.reply.libs.dto.client.question.QuestionCreateDto
import kotlinx.serialization.Serializable

@Serializable
data class TestUpdateDto(
    val title: String? = null,
    val type: Int? = null,
    val formula: String? = null,
    val metric: Int? = null,
    val questions: MutableList<QuestionCreateDto>? = null
)