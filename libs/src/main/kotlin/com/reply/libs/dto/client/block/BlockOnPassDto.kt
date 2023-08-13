package com.reply.libs.dto.client.block

import com.reply.libs.dto.client.test.TestOnPassDto
import kotlinx.serialization.Serializable

@Serializable
data class BlockOnPassDto (
    val id: Int,
    val createdAt: String,
    val updatedAt: String?,
    val name: String,
    val description: String,
    val tests: List<TestOnPassDto>
)