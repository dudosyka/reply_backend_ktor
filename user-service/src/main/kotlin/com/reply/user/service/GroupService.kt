package com.reply.user.service

import com.reply.libs.database.dao.CompanyDao
import com.reply.libs.database.dao.GroupDao
import com.reply.libs.database.models.GroupModel
import com.reply.libs.dto.client.group.GroupCreateClientDto
import com.reply.libs.dto.client.group.GroupCreateDto
import com.reply.libs.dto.client.group.GroupOutputClientDto
import com.reply.libs.dto.client.group.GroupOutputDto
import com.reply.libs.dto.internal.AuthorizedUser
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.database.idValue
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.instance

class GroupService(override val di: DI): CrudService<GroupOutputDto, GroupCreateDto, GroupDao>(di, GroupModel, GroupDao.Companion) {
    private val userService: UserService by instance()
    fun getAllForAuthorized(authorizedUser: AuthorizedUser) = getAll { GroupModel.company eq authorizedUser.companyId }.asDto()
    fun getOne(groupId: Int, authorizedUser: AuthorizedUser) = transaction {
        val group = getOne(groupId)

        if (group.companyId != authorizedUser.companyId)
            throw ForbiddenException()

        GroupOutputClientDto(
            group.idValue,
            group.name,
            group.companyId,
            group.users.asDto()
        )
    }
    fun create(groupCreateDto: GroupCreateClientDto, authorizedUser: AuthorizedUser) = transaction {
        GroupDao.new {
            name = groupCreateDto.name
            company = CompanyDao[authorizedUser.companyId]
        }.toOutputDto()
    }

}