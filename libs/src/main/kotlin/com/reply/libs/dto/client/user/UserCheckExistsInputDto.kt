package com.reply.libs.dto.client.user

import kotlinx.serialization.Serializable

@Serializable
data class UserCheckExistsInputDto (
    var login: String? = null,
    var phone: String? = null,
    var email: String? = null,
)