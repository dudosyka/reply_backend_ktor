package com.reply.libs.dto.client.metric

import kotlinx.serialization.Serializable

@Serializable
data class MetricOutputDto (
    val id: Int,
    val name: String,
    val description: String,
    val deleted: Boolean
)