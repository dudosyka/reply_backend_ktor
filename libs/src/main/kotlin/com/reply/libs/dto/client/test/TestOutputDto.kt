package com.reply.libs.dto.client.test

import com.reply.libs.dto.client.company.CompanyOutputDto
import com.reply.libs.dto.client.metric.MetricOutputDto
import com.reply.libs.dto.client.question.QuestionTypeOutputDto
import com.reply.libs.dto.client.user.TestAuthorOutputDto
import kotlinx.serialization.Serializable

@Serializable
data class TestOutputDto(
    val id: Int,
    val title: String,
    val type: QuestionTypeOutputDto,
    val company: CompanyOutputDto?,
    val formula: String,
    val author: TestAuthorOutputDto?,
    val metric: MetricOutputDto
)