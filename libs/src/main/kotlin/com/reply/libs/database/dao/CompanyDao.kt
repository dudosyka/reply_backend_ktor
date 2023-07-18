package com.reply.libs.database.dao

import com.reply.libs.database.models.CompanyModel
import com.reply.libs.dto.client.company.CompanyOutputDto
import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID

class CompanyDao(id: EntityID<Int>): BaseIntEntity<CompanyOutputDto>(id, CompanyModel) {
    companion object : BaseIntEntityClass<CompanyOutputDto, CompanyDao>(CompanyModel)

    var name by CompanyModel.name
    var logo by FileDao referencedOn CompanyModel.logo
    val logoId by CompanyModel.logo
    override fun toOutputDto(): CompanyOutputDto = CompanyOutputDto(
        idValue, name, logoId.value
    )
}