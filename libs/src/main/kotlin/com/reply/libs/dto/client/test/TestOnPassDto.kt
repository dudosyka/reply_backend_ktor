package com.reply.libs.dto.client.test

import com.reply.libs.dto.client.question.QuestionOnPassDto
import kotlinx.serialization.Serializable

@Serializable
data class TestOnPassDto (
    val id: Int,
    val title: String,
    val createdAt: String,
    val updatedAt: String?,
    val metric: Int,
    val questions: List<QuestionOnPassDto>
)