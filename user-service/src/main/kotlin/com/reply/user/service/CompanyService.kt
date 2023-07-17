package com.reply.user.service

import com.reply.libs.database.dao.CompanyDao
import com.reply.libs.dto.client.company.CompanyCreateDto
import com.reply.libs.consul.FileServiceClient
import com.reply.libs.database.models.CompanyModel
import com.reply.libs.database.models.UserModel
import com.reply.libs.dto.client.company.CompanyOutputDto
import com.reply.libs.dto.internal.AuthorizedUser
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.utils.crud.CrudService
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.instance

class CompanyService(override val di: DI) : CrudService<CompanyOutputDto, CompanyCreateDto>(di, CompanyModel, CompanyDao.Companion) {
    private val fileServiceClient: FileServiceClient by instance()
    private val userService: UserService by instance()
    suspend fun create(companyCreateDto: CompanyCreateDto, call: ApplicationCall) = newSuspendedTransaction {
        val companyLogo = fileServiceClient.uploadFile(call, companyCreateDto.logo)
        commit()
        try {
            insert(companyCreateDto) {
                this[CompanyModel.name] = it.name
                this[CompanyModel.logo] = companyLogo.id
            }
        } catch (e: Exception) {
            fileServiceClient.rollbackUploading(call, companyLogo.id)
            throw e
        }
    }

    fun getOne(companyId: Int, authorizedUser: AuthorizedUser): CompanyOutputDto = transaction {
        if (authorizedUser.companyId == companyId)
            getOne(companyId)
        else
            throw ForbiddenException()
    }

    fun getUsers(companyId: Int, authorizedUser: AuthorizedUser) = transaction {
        //Check is not Forbidden
        getOne(companyId, authorizedUser)

        userService.getAll { UserModel.company eq companyId }
    }
}