package com.reply.libs.dto.client.company

import com.reply.libs.dto.client.file.FileDto
import kotlinx.serialization.Serializable

@Serializable
data class CompanyDto (
    val id: Int,
    val name: String,
    val logo: FileDto
)