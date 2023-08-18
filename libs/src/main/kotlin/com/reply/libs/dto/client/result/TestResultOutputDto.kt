package com.reply.libs.dto.client.result

data class TestResultOutputDto (
    val testId: Int?,
    val metricId: Int,
    val testTitle: String,
    val value: Int,
    val createdAt: String
)