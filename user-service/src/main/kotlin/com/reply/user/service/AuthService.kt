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
import com.reply.libs.dto.client.signup.SignUpInputClientDto
import com.reply.libs.dto.client.block.BlockTokenDto
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

    suspend fun getToken(data : BlockTokenDto) : AuthOutputDto = transaction{
        try {
            val user = UserDao.find { UserModel.id eq data.userId }.toList().first()
            AuthOutputDto(
                createToken(
                    mutableMapOf(
                        "blockId" to data.blockId.toString(),
                        "week" to data.week.toString(),
                        "userId" to data.userId.toString(),
                        "id" to user.idValue.toString(),
                        "role" to user.role.idValue.toString(),
                        "login" to user.login,
                        "companyId" to user.company.idValue.toString()
                    )
                )
            )
        }catch (e : NoSuchElementException){
            throw ForbiddenException("User not found")
        }
    }
    
    private fun checkUnique(login: String, email: String, ) {
        if (!UserDao.find {
                (UserModel.login eq login) or (UserModel.email eq email)
            }.empty()) throw DuplicateEntryException("Login and Email must be unique")
    }

    suspend fun signUpAdmin(signUpInputDto: SignUpInputDto, call: ApplicationCall): SuccessOutputDto = transaction {
        checkUnique(signUpInputDto.login, signUpInputDto.email)

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
        } catch (e: ExposedSQLException) {
            rollback()
            fileServiceClient.rollbackUploading(call, company.logo)
            fileServiceClient.rollbackUploading(call, userLogo.id)
            throw e
        }
        commit()
        SuccessOutputDto(status = "success", msg = "Successfully signup")
    }

    suspend fun signUpClient(data: SignUpInputClientDto, call: ApplicationCall): SuccessOutputDto = transaction {
        checkUnique(data.login, data.email)

        val userLogo = fileServiceClient.uploadFile(call, data.avatar)

        try {
            userService.create(
                data.toUserCreateDto().apply {
                    role = RBACConfig.CLIENT.roleId
                    avatar = userLogo.id
                }
            )
        } catch (e: ExposedSQLException) {
            rollback()
            fileServiceClient.rollbackUploading(call, userLogo.id)
            throw e
        }
        commit()
        SuccessOutputDto(status = "success", msg = "Successfully signup")
    }
}