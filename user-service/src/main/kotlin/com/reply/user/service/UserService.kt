package com.reply.user.service

import com.reply.libs.database.dao.CompanyDao
import com.reply.libs.database.dao.FileDao
import com.reply.libs.database.dao.RoleDao
import com.reply.libs.database.dao.UserDao
import com.reply.libs.database.models.UserModel
import com.reply.libs.dto.client.user.UserCreateDto
import com.reply.libs.dto.client.user.UserOutputDto
import com.reply.libs.utils.crud.CrudService
import org.kodein.di.DI

class UserService(override val di: DI) : CrudService<UserOutputDto, UserCreateDto, UserDao>(di, UserModel, UserDao.Companion) {
    fun create(userCreateDto: UserCreateDto): UserDao =
        UserDao.new {
            login = userCreateDto.login
            avatar = if (userCreateDto.avatar != null) FileDao[userCreateDto.avatar!!] else null
            hash = userCreateDto.hash
            fullname = userCreateDto.fullname
            phone = userCreateDto.phone
            email = userCreateDto.email
            role = RoleDao[userCreateDto.role]
            company = CompanyDao[userCreateDto.company]
        }

    fun getByIds(ids: List<Int>) = getAll { UserModel.id inList ids }
}