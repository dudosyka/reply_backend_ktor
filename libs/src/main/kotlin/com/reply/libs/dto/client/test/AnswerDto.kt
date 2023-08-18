package com.reply.libs.dto.client.test

import kotlinx.serialization.Serializable

@Serializable
data class AnswerDto (
    val question_id: Int,
    val answer: List<Int>
)