package com.reply.libs.dto.client.user

import kotlinx.serialization.Serializable

@Serializable
class TestAuthorOutputDto (
    val id: Int,
    val login: String,
    val fullname: String
)