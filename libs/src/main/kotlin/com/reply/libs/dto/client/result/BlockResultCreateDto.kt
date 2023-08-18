package com.reply.libs.dto.client.result

import com.reply.libs.dto.client.test.TestResultDto
import kotlinx.serialization.Serializable

@Serializable
data class BlockResultCreateDto (
    val time_on_pass: Int,
    val tests: List<TestResultDto>
)