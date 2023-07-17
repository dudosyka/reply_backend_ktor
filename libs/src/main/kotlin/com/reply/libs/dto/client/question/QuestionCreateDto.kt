package com.reply.libs.dto.client.question

import com.reply.libs.dto.client.file.FileCreateDto
import kotlinx.serialization.Serializable

@Serializable
data class QuestionCreateDto (
    val title: String,
    val type: Int,
    val relative_id: Int,
    val value: MutableList<QuestionValueDto>,
    val coins: Int,
    val picture: FileCreateDto
)