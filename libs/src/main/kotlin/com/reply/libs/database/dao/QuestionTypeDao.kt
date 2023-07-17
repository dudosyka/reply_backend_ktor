package com.reply.libs.database.dao

import com.reply.libs.database.models.QuestionTypeModel
import com.reply.libs.dto.client.question.QuestionTypeOutputDto
import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID

class QuestionTypeDao(id: EntityID<Int>) : BaseIntEntity<QuestionTypeOutputDto>(id, QuestionTypeModel) {
    companion object : BaseIntEntityClass<QuestionTypeOutputDto, QuestionTypeDao>(QuestionTypeModel)

    val name by QuestionTypeModel.name
    override fun toOutputDto(): QuestionTypeOutputDto = QuestionTypeOutputDto(
        idValue, name
    )
}