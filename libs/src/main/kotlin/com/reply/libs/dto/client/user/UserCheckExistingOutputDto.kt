package com.reply.libs.dto.client.user

data class UserCheckExistingOutputDto (
    var login: Boolean,
    var email: Boolean,
    var phone: Boolean
) {
    fun checkAll(): Boolean = login || email || phone
}