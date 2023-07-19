package com.reply.user.service

import com.reply.libs.config.RBACConfig
import com.reply.libs.utils.database.idValue
import com.reply.libs.database.models.UserModel
import com.reply.libs.dto.client.auth.AuthInputDto
import com.reply.libs.dto.client.auth.AuthOutputDto
import com.reply.libs.database.dao.UserDao
import com.reply.libs.dto.client.signup.SignUpInputDto
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.internal.exceptions.DuplicateEntryException
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.plugins.createToken
import com.reply.libs.utils.bcrypt.PasswordUtil
import com.reply.libs.consul.FileServiceClient
import com.reply.libs.utils.database.TransactionalService
import io.ktor.server.application.*
import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.or
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class AuthService(override val di: DI) : DIAware, TransactionalService {
    private val fileServiceClient: FileServiceClient by instance()
    private val companyService: CompanyService by instance()
    private val userService: UserService by instance()
    suspend fun authUser(authInputDto: AuthInputDto): AuthOutputDto = transaction {
        val users = UserDao.find {
            UserModel.login eq authInputDto.login
        }.toList()

        if (users.isNotEmpty()) {
            val user = users.first()
            if (PasswordUtil.compare(authInputDto.password, user.hash)) {
                return@transaction AuthOutputDto(createToken(mutableMapOf(
                    "id" to user.idValue.toString(),
                    "role" to user.role.idValue.toString(),
                    "login" to user.login,
                    "companyId" to user.company.idValue.toString()
                )))
            } else
                throw ForbiddenException("Password incorrect")

        } else
            throw ForbiddenException("Login not found")
    }

    suspend fun signUpAdmin(signUpInputDto: SignUpInputDto, call: ApplicationCall): SuccessOutputDto = transaction {
        if (!UserDao.find {
            (UserModel.login eq signUpInputDto.login) or (UserModel.email eq signUpInputDto.email)
        }.empty()) throw DuplicateEntryException("Login and Email must be unique")


        val company = companyService.create(signUpInputDto.companyData, call)
        val userLogo = fileServiceClient.uploadFile(call, signUpInputDto.avatar)

        try {
            userService.create(
                signUpInputDto.toUserCreateDto().apply {
                    this.company = company.id
                    role = RBACConfig.ADMIN.roleId
                    avatar = userLogo.id
                }
            )
            SuccessOutputDto(msg = "Successfully signup")
        } catch (e: ExposedSQLException) {
            rollback()
            fileServiceClient.rollbackUploading(call, company.logo)
            fileServiceClient.rollbackUploading(call, userLogo.id)
            throw e
        }

        SuccessOutputDto("", "")
    }
}