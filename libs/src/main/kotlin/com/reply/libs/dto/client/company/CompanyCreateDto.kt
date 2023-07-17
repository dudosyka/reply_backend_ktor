package com.reply.libs.dto.client.company

import com.reply.libs.dto.client.file.FileCreateDto
import kotlinx.serialization.Serializable

@Serializable
data class CompanyCreateDto (
    val name: String,
    val logo: FileCreateDto
)