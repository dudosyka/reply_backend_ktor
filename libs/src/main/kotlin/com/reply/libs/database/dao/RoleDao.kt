package com.reply.libs.database.dao

import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.database.models.RoleModel
import com.reply.libs.dto.client.auth.RoleOutputDto
import com.reply.libs.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID

class RoleDao(id: EntityID<Int>): BaseIntEntity<RoleOutputDto>(id, RoleModel) {
    companion object : BaseIntEntityClass<RoleOutputDto, RoleDao>(RoleModel)

    var name by RoleModel.name
    var description by RoleModel.description
    override fun toOutputDto(): RoleOutputDto = RoleOutputDto(
        idValue, name, description
    )
}