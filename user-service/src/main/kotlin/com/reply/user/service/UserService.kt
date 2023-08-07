package com.reply.user.service

import com.reply.libs.config.RBACConfig
import com.reply.libs.consul.FileServiceClient
import com.reply.libs.database.dao.CompanyDao
import com.reply.libs.database.dao.FileDao
import com.reply.libs.database.dao.RoleDao
import com.reply.libs.database.dao.UserDao
import com.reply.libs.database.models.UserModel
import com.reply.libs.dto.client.user.*
import com.reply.libs.dto.internal.AuthorizedUser
import com.reply.libs.dto.internal.exceptions.DuplicateEntryException
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.dto.internal.exceptions.ModelNotFound
import com.reply.libs.utils.bcrypt.PasswordUtil
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.database.idValue
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.or
import org.kodein.di.DI
import org.kodein.di.instance

class UserService(override val di: DI) : CrudService<UserOutputDto, UserCreateDto, UserDao>(di, UserModel, UserDao.Companion) {
    private val fileServiceClient: FileServiceClient by instance()
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

    suspend fun getById(id: Int): UserOutputDto = transaction {
        UserDao.findById(id)?.toOutputDto() ?: throw ModelNotFound("User with id = $id not found")
    }

    private suspend fun getDaoById(id: Int): UserDao = transaction {
        UserDao.findById(id) ?: throw ModelNotFound("User with id = $id not found")
    }

    suspend fun patch(userData: UserUpdateDto, userId: Int, authorized: AuthorizedUser, call: ApplicationCall): UserOutputDto {
        val user = getDaoById(userId)

        //Check if user is not admin and try to update someone other account data
        if (authorized.role != RBACConfig.ADMIN.roleId && user.idValue != authorized.id)
            throw ForbiddenException()

        //Check if user is admin and try to update user from different company
        if (user.companyId != authorized.companyId)
            throw ForbiddenException()


        // Check if user try to update login or email or phone
        // We need to check if it has already taken
        val dataOnCheck = UserCheckExistsInputDto()
        if (user.login != userData.login)
            dataOnCheck.login = userData.login

        if (user.email != userData.email)
            dataOnCheck.email = userData.email

        if (user.phone != userData.phone)
            dataOnCheck.phone = userData.phone

        if (checkExisting(dataOnCheck).checkAll())
            throw DuplicateEntryException()

        val userAvatarId = user.avatarId

        transaction {
            user.apply {
                login = userData.login
                avatar = FileDao.findById(userData.avatar ?: 0)
                hash = PasswordUtil.hash(userData.password)
                fullname = userData.fullname
                phone = userData.phone
                email = userData.email
                flush()
            }
            commit()
        }

        if (userAvatarId != userData.avatar && userAvatarId != null) {
            fileServiceClient.rollbackUploading(call, userAvatarId)
        }

        return user.toOutputDto()
    }

    suspend fun checkExisting(dataOnCheck: UserCheckExistsInputDto): UserCheckExistingOutputDto {
         val check = transaction {
             getAll {
                 (UserModel.login eq (dataOnCheck.login ?: "")) or
                 (UserModel.email eq (dataOnCheck.email ?: "")) or
                 (UserModel.phone eq (dataOnCheck.phone ?: ""))
             }
         }

        val output = UserCheckExistingOutputDto(login = false, email = false, phone = false)

        check.forEach {
            if (it.login == dataOnCheck.login)
                output.login = true
            if (it.email == dataOnCheck.email)
                output.email = true
            if (it.phone == dataOnCheck.phone)
                output.phone = true
        }

        println(output)

        return output
    }
}