package com.reply.user.service

import com.reply.libs.database.dao.UserDao
import com.reply.libs.database.models.UserModel
import com.reply.libs.dto.client.user.UserCreateDto
import com.reply.libs.dto.client.user.UserOutputDto
import com.reply.libs.utils.crud.CrudService
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI

class UserService(override val di: DI) : CrudService<UserOutputDto, UserCreateDto>(di, UserModel, UserDao.Companion) {
    fun create(userCreateDto: UserCreateDto) = transaction {
        insert(userCreateDto) {
            this[UserModel.login] = it.login
            this[UserModel.avatar] = it.avatar
            this[UserModel.hash] = it.hash
            this[UserModel.fullname] = it.fullname
            this[UserModel.phone] = it.phone
            this[UserModel.email] = it.email
            this[UserModel.role] = it.role
            this[UserModel.company] = it.company
        }
    }
}