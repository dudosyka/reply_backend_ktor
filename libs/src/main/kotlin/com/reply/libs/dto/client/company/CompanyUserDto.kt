package com.reply.libs.dto.client.company

import kotlinx.serialization.Serializable

@Serializable
data class CompanyUserDto (
    val id: Int,
    val login: String,
    val fullname: String
)