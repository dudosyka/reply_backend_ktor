package com.reply.test.service

import com.reply.libs.database.dao.QuestionDao
import com.reply.libs.database.models.QuestionModel
import com.reply.libs.dto.client.question.QuestionCreateDto
import com.reply.libs.dto.client.question.QuestionOutputDto
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.crud.asDto
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI

class QuestionService(override val di: DI): CrudService<QuestionOutputDto, QuestionCreateDto, QuestionDao>(di, QuestionModel, QuestionDao.Companion)  {
    fun createBatch(testId: Int, questions: MutableList<QuestionCreateDto>): List<QuestionOutputDto> = transaction {
        insert(questions) {
            this[QuestionModel.title] = it.title
            this[QuestionModel.type] = it.type
            this[QuestionModel.test] = testId
            this[QuestionModel.relative_id] = it.relative_id
            this[QuestionModel.value] = QuestionDao.encodeValue(it.value)
            this[QuestionModel.coins] = it.coins
        }.asDto()
    }

    fun updateForTest(testId: Int, questions: MutableList<QuestionCreateDto>) = transaction {
        //Delete old questions
        delete {
            QuestionModel.test eq testId
        }

        createBatch(testId, questions)
    }
}