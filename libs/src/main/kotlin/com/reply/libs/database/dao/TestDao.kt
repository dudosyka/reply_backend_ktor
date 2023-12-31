package com.reply.libs.database.dao

import com.reply.libs.database.models.QuestionModel
import com.reply.libs.database.models.TestModel
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.sql.transactions.transaction

class TestDao(id: EntityID<Int>) : BaseIntEntity<TestOutputDto>(id, TestModel) {
    companion object: BaseIntEntityClass<TestOutputDto, TestDao>(TestModel)

    var title by TestModel.title
    var type by QuestionTypeDao referencedOn TestModel.type
    val typeId by TestModel.type
    var company by CompanyDao optionalReferencedOn TestModel.company
    private val _companyId by TestModel.company
    val companyId
        get() = _companyId?.value
    var formula by TestModel.formula
    var author by UserDao optionalReferencedOn TestModel.author
    val authorId by TestModel.author
    var metric by MetricDao referencedOn TestModel.metric
    private val _metricId by TestModel.metric
    val metricId
        get() = _metricId.value
    val questions by QuestionDao referrersOn QuestionModel.test

//    var questions by QuestionDao via QuestionModel
    override fun toOutputDto(): TestOutputDto = transaction { TestOutputDto(
        idValue,
        title,
        typeId.value,
        companyId,
        formula,
        authorId?.value,
        metricId
    ) }
}