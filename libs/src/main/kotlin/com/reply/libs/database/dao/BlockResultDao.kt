package com.reply.libs.database.dao

import com.reply.libs.database.models.result.BlockResultModel
import com.reply.libs.database.models.result.BlockTestResultModel
import com.reply.libs.dto.client.result.BlockResultOutputDto
import com.reply.libs.utils.crud.asDto
import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class BlockResultDao(id: EntityID<Int>): BaseIntEntity<BlockResultOutputDto>(id, BlockResultModel) {
    companion object : BaseIntEntityClass<BlockResultOutputDto, BlockResultDao>(BlockResultModel)

    var time by BlockResultModel.time
    var week by BlockResultModel.week
    var blockTitle by BlockResultModel.blockTitle
    var companyTitle by BlockResultModel.companyTitle
    var isValid by BlockResultModel.isValid
    var user by UserDao referencedOn BlockResultModel.user
    private val _userId by BlockResultModel.user
    val userId: Int
        get() = _userId.value
    var block by BlockDao optionalReferencedOn BlockResultModel.block
    private val _blockId by BlockResultModel.block
    val blockId: Int?
        get() = _blockId?.value
    var company by CompanyDao optionalReferencedOn BlockResultModel.company
    private val _companyId by BlockResultModel.company
    val companyId: Int?
        get() = _companyId?.value
    var testResults by TestResultDao via BlockTestResultModel
    override fun toOutputDto(): BlockResultOutputDto = BlockResultOutputDto(
        blockId, companyId, userId, isValid, companyTitle, blockTitle, week, time, testResults.asDto(), createdAt.toString()
    )
}