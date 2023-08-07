package com.reply.libs.dto.client.test

import com.reply.libs.dto.client.question.QuestionCreateDto
import kotlinx.serialization.Serializable

@Serializable
data class TestCreateDto (
    var title: String,
    val type: Int,
    val formula: String,
    val metric: Int,
    val questions: MutableList<QuestionCreateDto>
)