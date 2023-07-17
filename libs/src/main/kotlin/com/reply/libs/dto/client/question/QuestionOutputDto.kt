package com.reply.libs.dto.client.question

import com.reply.libs.dto.client.file.FileOutputDto
import com.reply.libs.dto.client.test.TestOutputDto
import kotlinx.serialization.Serializable

@Serializable
data class QuestionOutputDto (
    val id: Int,
    val title: String,
    val type: QuestionTypeOutputDto,
    val test: TestOutputDto,
    val relative_id: Int,
    val value: MutableList<QuestionValueDto>,
    val coins: Int,
    val picture: FileOutputDto?
)