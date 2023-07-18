package com.reply.libs.dto.client.user

import com.reply.libs.dto.client.company.CompanyUserDto
import kotlinx.serialization.Serializable

@Serializable
data class UserOutputDto (
    val id: Int,
    val login: String,
    val avatar: Int?,
    val hash: String,
    val fullname: String,
    val phone: String,
    val emailCode: Int?,
    val email: String,
    val role: Int,
    val coins: Int,
    val company: Int
) {
    fun toCompanyUser(): CompanyUserDto =
        CompanyUserDto(id, login, fullname)
}