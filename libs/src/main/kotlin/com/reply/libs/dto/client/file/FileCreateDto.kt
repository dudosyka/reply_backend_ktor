package com.reply.libs.dto.client.file

import kotlinx.serialization.Serializable

@Serializable
data class FileCreateDto (
    val fileName: String,
    val base64Encoded: String
)