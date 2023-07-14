package com.reply.user.service

import com.reply.libs.config.database.idValue
import com.reply.libs.database.models.UserModel
import com.reply.libs.dto.auth.request.AuthDto
import com.reply.libs.dto.auth.response.AuthOutputDto
import com.reply.libs.database.dao.UserDao
import com.reply.libs.dto.exceptions.ForbiddenException
import com.reply.libs.plugins.createToken
import org.jetbrains.exposed.sql.transactions.transaction

class AuthService {
    fun authUser(authDto: AuthDto): AuthOutputDto {
//        createToken(mutableMapOf(
//            "login" to authDto.login,
//            "role" to "${if (authDto.login == "admin") 1 else if (authDto.login == "client") 2 else 0}"
//        ))
        return transaction {
            val users = UserDao.find {
                UserModel.login eq authDto.login
            }.toList()

            if (users.isNotEmpty()) {
                val user = users.first()
                if (user.hash == authDto.password) {
                    return@transaction AuthOutputDto(createToken(mutableMapOf(
                        "id" to user.idValue.toString(),
                        "role" to user.role.toString(),
                        "login" to user.login
                    )))
                } else
                    throw ForbiddenException("Password incorrect")

            } else
                throw ForbiddenException("Login not found")
        }
    }
}