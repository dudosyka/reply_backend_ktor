package com.reply.libs.database.dao

import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.database.models.CompanyModel
import org.jetbrains.exposed.dao.id.EntityID

class CompanyDao(id: EntityID<Int>): BaseIntEntity(id, CompanyModel) {
    companion object : BaseIntEntityClass<CompanyDao>(CompanyModel)

    var name by CompanyModel.name
    var logo by FileDao referencedOn CompanyModel.logo
}