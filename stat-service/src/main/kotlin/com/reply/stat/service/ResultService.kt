package com.reply.stat.service

import com.reply.libs.database.dao.*
import com.reply.libs.database.models.TestModel
import com.reply.libs.database.models.result.BlockResultModel
import com.reply.libs.database.models.result.TestResultModel
import com.reply.libs.dto.client.result.BlockResultCreateDto
import com.reply.libs.dto.client.question.QuestionValueDto
import com.reply.libs.dto.client.result.BlockResultOutputDto
import com.reply.libs.dto.client.result.TestResultCreateDto
import com.reply.libs.dto.client.test.TestResultDto
import com.reply.libs.dto.internal.AuthorizedUser
import com.reply.libs.dto.internal.exceptions.BadRequestException
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.dto.internal.exceptions.ModelNotFound
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.database.idValue
import com.reply.stat.utils.formula.Formula
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.SizedCollection
import org.jetbrains.exposed.sql.batchInsert
import org.kodein.di.DI

class ResultService(override val di: DI): CrudService<BlockResultOutputDto, BlockResultCreateDto, BlockResultDao>(di, BlockResultModel, BlockResultDao.Companion) {
    suspend fun passBlock(data: BlockResultCreateDto, authorizedUser: AuthorizedUser): Boolean = transaction {
        //If token doesn`t contain block and week it is not valid for passing
        val blockId = authorizedUser.blockId ?: throw ForbiddenException()
        val blockDao = BlockDao.findById(blockId) ?: throw ModelNotFound()
        val weekOnPass  = authorizedUser.week ?: throw ForbiddenException()

        val companyDao = CompanyDao.findById(authorizedUser.companyId) ?: throw ModelNotFound()

        val testsResults = data.tests.associateBy { it.test_id }
        val tests = TestDao.find {
            TestModel.id inList data.tests.map { it.test_id }
        }

        if (tests.count() != data.tests.size.toLong())
            throw ModelNotFound("Not all tests can be recognized")

        val onCreate: MutableList<TestResultCreateDto> = mutableListOf()
        tests.forEach {
            onCreate.add(
                TestResultCreateDto(
                    it, countTestResult(testsResults[it.idValue] ?: throw ModelNotFound(), it)
                )
            )
        }

        val results = TestResultModel.batchInsert(onCreate) {
            this[TestResultModel.test] = it.test.idValue
            this[TestResultModel.testTitle] = it.test.title
            this[TestResultModel.metric] = it.test.metricId
            this[TestResultModel.value] = it.value
            this[TestResultModel.updatedAt] = null
        }.map { TestResultDao.wrapRow(it) }

        BlockResultDao.new {
            time = data.time_on_pass
            week = weekOnPass
            blockTitle = blockDao.name
            companyTitle = companyDao.name
            isValid = checkValidResult(data, blockDao)
            user = UserDao[authorizedUser.id]
            block = blockDao
            company = companyDao
            testResults = SizedCollection(results)
        }

        commit()

        true
    }

    private suspend fun countTestResult(result: TestResultDto, test: TestDao): Int = transaction {
        val valueByQuestion = mutableMapOf<Int, Int>()
        val questions: Map<Int, List<QuestionValueDto>> = test.questions.associate {
            Pair(it.relative_id, Json.decodeFromString<List<QuestionValueDto>>(it.value))
        }
        result.answers.forEach { answerDto ->
            val questionData = questions[answerDto.question_id] ?: throw BadRequestException()
            valueByQuestion[answerDto.question_id] = answerDto.answer.map {
                questionData[it - 1].value
            }.reduce { s, t -> s + t }
        }
        val formula = Formula(test.formula, valueByQuestion)

        formula.calc()
    }

    private fun checkValidResult(result: BlockResultCreateDto, block: BlockDao): Boolean {
        return (block.time >= result.time_on_pass)
    }

}