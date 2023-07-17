package com.reply.libs.database.dao

import com.reply.libs.database.models.QuestionModel
import com.reply.libs.dto.client.question.QuestionOutputDto
import com.reply.libs.dto.client.question.QuestionValueDto
import com.reply.libs.utils.database.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.dao.id.EntityID

class QuestionDao(id: EntityID<Int>) : BaseIntEntity<QuestionOutputDto>(id, QuestionModel) {
    companion object : BaseIntEntityClass<QuestionOutputDto, QuestionDao>(QuestionModel) {
        fun encodeValue(value: MutableList<QuestionValueDto>) = Json.encodeToString(value)
    }

    val title by QuestionModel.title
    val type by QuestionTypeDao referencedOn QuestionModel.type
    val test by TestDao referencedOn QuestionModel.test
    val relative_id by QuestionModel.relative_id
    val value: String by QuestionModel.value
    val coins by QuestionModel.coins
    val picture by FileDao optionalReferencedOn QuestionModel.picture

    override fun toOutputDto(): QuestionOutputDto = QuestionOutputDto(
        idValue, title, type.toOutputDto(), test.toOutputDto(), relative_id, Json.decodeFromString(value), coins, picture?.toOutputDto()
    )

}