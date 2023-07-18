package com.reply.libs.database.dao

import com.reply.libs.database.models.GroupModel
import com.reply.libs.database.models.GroupUsersModel
import com.reply.libs.dto.client.group.GroupOutputClientDto
import com.reply.libs.dto.client.group.GroupOutputDto
import com.reply.libs.utils.crud.asDto
import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID

class GroupDao(id: EntityID<Int>): BaseIntEntity<GroupOutputDto>(id, GroupModel) {
    companion object: BaseIntEntityClass<GroupOutputDto, GroupDao>(GroupModel)

    var name by GroupModel.name
    var company by CompanyDao referencedOn GroupModel.company
    private val _companyId by GroupModel.company
    val companyId
        get() = _companyId.value
    var users by UserDao via GroupUsersModel

    override fun toOutputDto(): GroupOutputDto = GroupOutputDto(
        idValue, name, companyId, mutableListOf()
    )

    fun toClientOutput(): GroupOutputClientDto = GroupOutputClientDto(
        idValue, name, companyId, users.asDto()
    )
}