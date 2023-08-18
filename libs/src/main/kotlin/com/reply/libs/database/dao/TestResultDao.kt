package com.reply.libs.database.dao

import com.reply.libs.database.models.result.TestResultModel
import com.reply.libs.dto.client.result.TestResultOutputDto
import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class TestResultDao(id: EntityID<Int>): BaseIntEntity<TestResultOutputDto>(id, TestResultModel) {
    companion object : BaseIntEntityClass<TestResultOutputDto, TestResultDao>(TestResultModel)

    val test by TestDao optionalReferencedOn  TestResultModel.test
    private val _testId by TestResultModel.test
    val testId: Int?
        get() = _testId?.value

    val metric by MetricDao referencedOn   TestResultModel.metric
    private val _metricId by TestResultModel.metric
    val metricId: Int
        get() = _metricId.value

    val testTitle by TestResultModel.testTitle
    val value by TestResultModel.value

    override fun toOutputDto(): TestResultOutputDto = TestResultOutputDto(
        testId, metricId, testTitle, value, createdAt.toString()
    )
}