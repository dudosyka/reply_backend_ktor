package com.reply.libs.dto.client.company

import com.reply.libs.dto.client.file.FileOutputDto
import kotlinx.serialization.Serializable

@Serializable
data class CompanyOutputDto (
    val id: Int,
    val name: String,
    val logo: FileOutputDto
)