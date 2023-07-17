package com.reply.libs.database.dao

import com.reply.libs.database.models.MetricModel
import com.reply.libs.dto.client.metric.MetricOutputDto
import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID

class MetricDao(id: EntityID<Int>) : BaseIntEntity<MetricOutputDto>(id, MetricModel) {
    companion object : BaseIntEntityClass<MetricOutputDto, MetricDao>(MetricModel)

    val name by MetricModel.name
    val description by MetricModel.description
    val deleted by MetricModel.deleted
    override fun toOutputDto(): MetricOutputDto = MetricOutputDto(
        idValue, name, description, deleted
    )
}