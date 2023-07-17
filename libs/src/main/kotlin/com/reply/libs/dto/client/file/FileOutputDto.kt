package com.reply.libs.dto.client.file

import kotlinx.serialization.Serializable

@Serializable
data class FileOutputDto (
    val id: Int,
    val path: String
)