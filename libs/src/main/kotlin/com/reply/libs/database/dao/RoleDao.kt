package com.reply.libs.database.dao

import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.database.models.RoleModel
import org.jetbrains.exposed.dao.id.EntityID

class RoleDao(id: EntityID<Int>): BaseIntEntity(id, RoleModel) {
    companion object : BaseIntEntityClass<RoleDao>(RoleModel)

    var name by RoleModel.name
    var description by RoleModel.description
}