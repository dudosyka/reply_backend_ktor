package com.reply.libs.dto.client.file

import com.reply.libs.dto.client.base.OutputDto
import kotlinx.serialization.Serializable

@Serializable
data class FileDto (
    val id: Int,
    val path: String
): OutputDto