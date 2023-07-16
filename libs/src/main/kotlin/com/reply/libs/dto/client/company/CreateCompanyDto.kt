package com.reply.libs.dto.client.company

import com.reply.libs.dto.client.file.CreateFileDto
import kotlinx.serialization.Serializable

@Serializable
data class CreateCompanyDto (
    val name: String,
    val logo: CreateFileDto
)