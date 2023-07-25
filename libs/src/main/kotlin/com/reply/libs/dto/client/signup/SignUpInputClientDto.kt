package com.reply.libs.dto.client.signup

import com.reply.libs.dto.client.file.FileCreateDto
import com.reply.libs.dto.client.user.UserCreateDto
import com.reply.libs.utils.bcrypt.PasswordUtil

data class SignUpInputClientDto (
    val login: String,
    val avatar: FileCreateDto,
    val password: String,
    val fullname: String,
    val phone: String,
    val email: String,
    val company: Int,
) {
    fun toUserCreateDto(): UserCreateDto =
        UserCreateDto(
            login,
            0,
            PasswordUtil.hash(password),
            fullname,
            phone,
            email,
            0,
            0,
            company
        )
}
