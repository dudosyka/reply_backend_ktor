package com.reply.libs.dto.client.result

import com.reply.libs.database.dao.TestDao

data class TestResultCreateDto(
    val test: TestDao,
    val value: Int
)