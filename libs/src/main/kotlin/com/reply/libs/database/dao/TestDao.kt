package com.reply.libs.database.dao

import com.reply.libs.database.models.TestModel
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.dto.client.user.TestAuthorOutputDto
import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class TestDao(id: EntityID<Int>) : BaseIntEntity<TestOutputDto>(id, TestModel) {
    companion object: BaseIntEntityClass<TestOutputDto, TestDao>(TestModel)

    var title by TestModel.title
    var type by QuestionTypeDao referencedOn TestModel.type
    var company by CompanyDao optionalReferencedOn TestModel.company
    var formula by TestModel.formula
    var author by UserDao optionalReferencedOn TestModel.author
    var metric by MetricDao referencedOn TestModel.metric

//    var questions by QuestionDao via QuestionModel
    override fun toOutputDto(): TestOutputDto = transaction { TestOutputDto(
        idValue,
        title,
        type.toOutputDto(),
        company?.toOutputDto(),
        formula,
        if (author != null) TestAuthorOutputDto(author!!.idValue, author!!.login, author!!.fullname) else null,
        metric.toOutputDto()
    ) }
}