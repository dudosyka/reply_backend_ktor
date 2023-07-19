package com.reply.user.service

import com.reply.libs.database.dao.CompanyDao
import com.reply.libs.database.dao.GroupDao
import com.reply.libs.database.models.GroupModel
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.group.GroupCreateClientDto
import com.reply.libs.dto.client.group.GroupCreateDto
import com.reply.libs.dto.client.group.GroupOutputClientDto
import com.reply.libs.dto.client.group.GroupOutputDto
import com.reply.libs.dto.internal.AuthorizedUser
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.crud.asDto
import org.jetbrains.exposed.sql.SizedCollection
import org.kodein.di.DI
import org.kodein.di.instance

class GroupService(override val di: DI): CrudService<GroupOutputDto, GroupCreateDto, GroupDao>(di, GroupModel, GroupDao.Companion) {
    private val userService: UserService by instance()
    fun getAllForAuthorized(authorizedUser: AuthorizedUser) = getAll { GroupModel.company eq authorizedUser.companyId }.asDto()
    private suspend fun getOne(groupId: Int, authorizedUser: AuthorizedUser) = transaction {
        val group = getOne(groupId)

        if (group.companyId != authorizedUser.companyId)
            throw ForbiddenException()

        group
    }
    suspend fun get(groupId: Int, authorizedUser: AuthorizedUser) = transaction {
        val group = getOne(groupId)

        if (group.companyId != authorizedUser.companyId)
            throw ForbiddenException()

        group.toClientOutput()
    }
    suspend fun create(groupCreateDto: GroupCreateClientDto, authorizedUser: AuthorizedUser): GroupOutputClientDto = transaction {
        val groupUsers = userService.getByIds(groupCreateDto.users)
        GroupDao.new {
            name = groupCreateDto.name
            company = CompanyDao[authorizedUser.companyId]
            users = SizedCollection(groupUsers)
        }.toClientOutput()
    }

    suspend fun delete(groupId: Int, authorizedUser: AuthorizedUser): SuccessOutputDto = transaction {
        getOne(groupId, authorizedUser).delete()

        SuccessOutputDto("success", "Group successfully deleted")
    }

    suspend fun update(groupId: Int, groupUpdateDto: GroupCreateClientDto, authorizedUser: AuthorizedUser): GroupOutputClientDto = transaction {
        getOne(groupId, authorizedUser).apply {
            name = groupUpdateDto.name
            users = SizedCollection(userService.getByIds(groupUpdateDto.users))
            flush()
        }.toClientOutput()
    }

}