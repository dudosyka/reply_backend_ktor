package com.reply.test.service

import com.reply.libs.database.dao.QuestionDao
import com.reply.libs.database.models.QuestionModel
import com.reply.libs.dto.client.question.QuestionCreateDto
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.deleteWhere
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.DIAware

class QuestionService(override val di: DI) : DIAware {
    fun createBatch(testId: Int, questions: MutableList<QuestionCreateDto>): List<QuestionDao> = transaction {
        QuestionModel.batchInsert(questions) {
            this[QuestionModel.title] = it.title
            this[QuestionModel.type] = it.type
            this[QuestionModel.test] = testId
            this[QuestionModel.relative_id] = it.relative_id
            this[QuestionModel.value] = QuestionDao.encodeValue(it.value)
            this[QuestionModel.coins] = it.coins
        }.map {
            QuestionDao.wrapRow(it)
        }
    }

    fun updateForTest(testId: Int, questions: MutableList<QuestionCreateDto>) = transaction {
        //Delete old questions
        QuestionModel.deleteWhere {
            test eq testId
        }

        createBatch(testId, questions)
    }
}