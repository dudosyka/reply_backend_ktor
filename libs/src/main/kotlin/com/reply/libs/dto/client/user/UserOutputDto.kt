package com.reply.libs.dto.client.user

import com.reply.libs.dto.client.auth.RoleOutputDto
import com.reply.libs.dto.client.company.CompanyOutputDto
import com.reply.libs.dto.client.file.FileOutputDto
import kotlinx.serialization.Serializable

@Serializable
data class UserOutputDto (
    val id: Int,
    val login: String,
    val avatar: FileOutputDto?,
    val hash: String,
    val fullname: String,
    val phone: String,
    val emailCode: Int?,
    val email: String,
    val role: RoleOutputDto,
    val coins: Int,
    val company: CompanyOutputDto
)