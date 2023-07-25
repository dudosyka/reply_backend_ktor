package com.reply.user.service

import com.reply.libs.consul.FileServiceClient
import com.reply.libs.database.dao.CompanyDao
import com.reply.libs.database.dao.FileDao
import com.reply.libs.database.models.CompanyModel
import com.reply.libs.database.models.GroupModel
import com.reply.libs.database.models.UserModel
import com.reply.libs.dto.client.company.CompanyCreateDto
import com.reply.libs.dto.client.company.CompanyOutputDto
import com.reply.libs.dto.client.company.CompanyUserDto
import com.reply.libs.dto.client.group.GroupOutputDto
import com.reply.libs.dto.internal.AuthorizedUser
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.crud.asDto
import com.reply.libs.utils.database.idValue
import io.ktor.server.application.*
import org.kodein.di.DI
import org.kodein.di.instance

class CompanyService(override val di: DI) : CrudService<CompanyOutputDto, CompanyCreateDto, CompanyDao>(di, CompanyModel, CompanyDao.Companion) {
    private val fileServiceClient: FileServiceClient by instance()
    private val userService: UserService by instance()
    private val groupService: GroupService by instance()
    suspend fun create(companyCreateDto: CompanyCreateDto, call: ApplicationCall): CompanyOutputDto = transaction {
        val companyLogo = fileServiceClient.uploadFile(call, companyCreateDto.logo)

        commit()

        try {
            insert(companyCreateDto) {
                this[CompanyModel.name] = it.name
                this[CompanyModel.logo] = companyLogo.id
            }.asDto()
        } catch (e: Exception) {
            fileServiceClient.rollbackUploading(call, companyLogo.id)
            throw e
        }
    }

    suspend fun getOne(companyId: Int, authorizedUser: AuthorizedUser): CompanyDao = transaction {
        if (authorizedUser.companyId == companyId)
            getOne(companyId)
        else
            throw ForbiddenException()
    }

    suspend fun getUsers(companyId: Int, authorizedUser: AuthorizedUser): List<CompanyUserDto> = transaction {
        //Check is not Forbidden
        getOne(companyId, authorizedUser)

        userService.getAll { UserModel.company eq companyId }.map { CompanyUserDto(it.idValue, it.login, it.fullname) }
    }

    suspend fun getGroups(companyId: Int, authorizedUser: AuthorizedUser): List<GroupOutputDto> = transaction {
        //Check is not Forbidden
        getOne(companyId, authorizedUser)

        groupService.getAll { GroupModel.company eq companyId }.asDto()
    }

    suspend fun update(companyId: Int, companyUpdateDto: CompanyCreateDto, call: ApplicationCall, authorizedUser: AuthorizedUser): CompanyOutputDto = transaction {
        if (companyId != authorizedUser.companyId)
            throw ForbiddenException()

        val companyLogo = fileServiceClient.uploadFile(call, companyUpdateDto.logo)

        try {
            val company = getOne(companyId, authorizedUser)
            val oldLogo = company.logoId.value

            commit()

            company.apply {
                name = companyUpdateDto.name
                logo = FileDao[companyLogo.id]
            }

            company.flush()
            commit()
            fileServiceClient.rollbackUploading(call, oldLogo)
            company.asDto()
        } catch (e: Exception) {
            fileServiceClient.rollbackUploading(call, companyLogo.id)
            throw e
        }
    }

    fun get(companyId: Int, authorizedUser: AuthorizedUser): CompanyOutputDto {
        if (companyId != authorizedUser.companyId)
            throw ForbiddenException()

        return CompanyDao[companyId].asDto()
    }
}