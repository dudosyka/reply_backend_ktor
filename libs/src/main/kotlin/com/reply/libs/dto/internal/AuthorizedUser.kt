package com.reply.libs.dto.internal

data class AuthorizedUser (
    val id: Int,
    val login: String,
    val role: Int,
    val companyId: Int,
    val blockId: Int? = null,
    val week: Int? = null
)