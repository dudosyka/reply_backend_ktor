package com.reply.user.service

import com.reply.libs.dto.auth.request.AuthDto
import com.reply.libs.plugins.createToken

class AuthService {
    fun authUser(authDto: AuthDto) = createToken(mutableMapOf(
            "login" to authDto.login,
            "role" to "${if (authDto.login == "admin") 1 else if (authDto.login == "client") 2 else 0}"
        ))
}