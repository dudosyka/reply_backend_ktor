package com.reply.libs.dto.client.test

import kotlinx.serialization.Serializable

@Serializable
data class TestResultDto (
    val test_id: Int,
    val answers: List<AnswerDto>
)