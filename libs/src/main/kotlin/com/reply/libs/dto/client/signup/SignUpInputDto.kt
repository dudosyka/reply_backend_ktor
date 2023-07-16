package com.reply.libs.dto.client.signup

import com.reply.libs.dto.client.company.CreateCompanyDto
import com.reply.libs.dto.client.file.CreateFileDto
import kotlinx.serialization.Serializable

@Serializable
data class SignUpInputDto(
    val login: String,
    val avatar: CreateFileDto,
    val password: String,
    val fullname: String,
    val phone: String,
    val email: String,
    val companyData: CreateCompanyDto
)
