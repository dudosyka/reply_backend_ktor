package com.reply.libs.dto.client.signup

import com.reply.libs.dto.client.company.CompanyCreateDto
import com.reply.libs.dto.client.file.FileCreateDto
import kotlinx.serialization.Serializable

@Serializable
data class SignUpInputDto(
    val login: String,
    val avatar: FileCreateDto,
    val password: String,
    val fullname: String,
    val phone: String,
    val email: String,
    val companyData: CompanyCreateDto
)
