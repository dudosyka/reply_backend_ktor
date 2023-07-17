package com.reply.libs.dto.client.user

data class UserCreateDto (
    val login: String,
    var avatar: Int?,
    val hash: String,
    val fullname: String,
    val phone: String,
    val email: String,
    var role: Int,
    var coins: Int,
    var company: Int
)