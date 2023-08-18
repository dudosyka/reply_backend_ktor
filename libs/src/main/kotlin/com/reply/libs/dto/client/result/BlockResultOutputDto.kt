package com.reply.libs.dto.client.result

data class BlockResultOutputDto (
    val blockId: Int?,
    val companyId: Int?,
    val userId: Int,
    val isValid: Boolean,
    val companyTitle: String,
    val blockTitle: String,
    val week: Int,
    val time: Int,
    val data: List<TestResultOutputDto>,
    val createdAt: String,
)