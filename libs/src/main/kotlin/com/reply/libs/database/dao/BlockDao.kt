package com.reply.libs.database.dao

import com.reply.libs.database.models.BlockModel
import com.reply.libs.database.models.BlockTestsModel
import com.reply.libs.dto.client.block.BlockOutputDto
import com.reply.libs.utils.crud.asDto
import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID

class BlockDao(id : EntityID<Int>) : BaseIntEntity<BlockOutputDto>(id, BlockModel) {
    companion object : BaseIntEntityClass<BlockOutputDto, BlockDao>(BlockModel)

    var name  by BlockModel.name
    var description by BlockModel.description
    var time by BlockModel.time
    var company by CompanyDao referencedOn BlockModel.company
    var tests by TestDao via BlockTestsModel

    override fun toOutputDto(): BlockOutputDto = BlockOutputDto(
        idValue, name, mutableListOf()
    )
    fun toClientOutput(): BlockOutputDto = BlockOutputDto(
        idValue, name, tests.asDto()
    )
}