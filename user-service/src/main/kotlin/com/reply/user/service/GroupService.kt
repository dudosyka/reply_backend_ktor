package com.reply.user.service

import com.reply.libs.database.dao.CompanyDao
import com.reply.libs.database.dao.GroupDao
import com.reply.libs.database.dao.UserDao
import com.reply.libs.database.models.GroupModel
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.group.*
import com.reply.libs.dto.internal.AuthorizedUser
import com.reply.libs.dto.internal.exceptions.BadRequestException
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.crud.asDto
import org.jetbrains.exposed.sql.SizedCollection
import org.kodein.di.DI
import org.kodein.di.instance

class GroupService(override val di: DI): CrudService<GroupOutputDto, GroupCreateDto, GroupDao>(di, GroupModel, GroupDao.Companion) {
    private val userService: UserService by instance()
    fun getAllForAuthorized(authorizedUser: AuthorizedUser) = getAll { GroupModel.company eq authorizedUser.companyId }.asDto()
    private suspend fun getOne(groupId: Int, authorizedUser: AuthorizedUser): GroupDao = transaction {
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
        val model = GroupDao.new {
            name = groupCreateDto.name
            company = CompanyDao[authorizedUser.companyId]
            users = SizedCollection(groupUsers)
        }
        commit()
        model.toClientOutput()
    }

    suspend fun delete(groupId: Int, authorizedUser: AuthorizedUser): SuccessOutputDto = transaction {
        getOne(groupId, authorizedUser).delete()
        commit()
        SuccessOutputDto("success", "Group successfully deleted")
    }

    suspend fun update(groupId: Int, groupUpdateDto: GroupCreateClientDto, authorizedUser: AuthorizedUser): GroupOutputClientDto = transaction {
        getOne(groupId, authorizedUser).apply {
            val clients = userService.getByIds(groupUpdateDto.users).map {
                if (it.companyId != authorizedUser.companyId)
                    throw ForbiddenException()
                it
            }
            name = groupUpdateDto.name
            users = SizedCollection(clients)
            flush()
            commit()
        }.toClientOutput()
    }

    private fun checkPermissions(groupId: Int, userId: Int, authorizedUser: AuthorizedUser): Pair<UserDao, GroupDao> {
        val group = GroupDao[groupId]
        val user = UserDao[userId]

        //Check that user, group and admin are all from the same company
        if (group.companyId != authorizedUser.companyId || user.companyId != authorizedUser.companyId)
            throw ForbiddenException()

        return Pair(user, group)
    }

    suspend fun appendUser(groupId: Int, userId: Int, authorizedUser: AuthorizedUser): SuccessOutputDto = transaction {
        val data = checkPermissions(groupId, userId, authorizedUser)
        val user = data.first
        val group = data.second

        if (group.users.contains(user))
            throw BadRequestException("User has already appended")

        group.apply {
            users = SizedCollection(users.plus(user))
        }

        group.flush()

        commit()

        SuccessOutputDto("success", "User successfully added to group")
    }

    suspend fun removeUser(groupId: Int, userId: Int, authorizedUser: AuthorizedUser): SuccessOutputDto = transaction {
        val data = checkPermissions(groupId, userId, authorizedUser)
        val user = data.first
        val group = data.second

        if (!user.groups.contains(group))
            throw BadRequestException("User is not in this group")

        group.apply {
            users = SizedCollection(users.minus(user))
        }

        group.flush()

        commit()

        SuccessOutputDto("success", "User successfully removed from group")
    }

}