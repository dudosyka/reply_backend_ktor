package com.reply.libs.dto.client.user

import kotlinx.serialization.Serializable

@Serializable
data class UserOutputDto (
    val id: Int,
    var login: String,
    var avatar: Int?,
    val hash: String,
    var fullname: String,
    var phone: String,
    val emailCode: Int?,
    var email: String,
    val role: Int,
    val coins: Int,
    val company: Int
) {
}