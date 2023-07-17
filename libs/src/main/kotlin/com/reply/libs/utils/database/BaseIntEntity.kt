package com.reply.libs.utils.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID

abstract class BaseIntEntity<OutputDto>(id: EntityID<Int>, table: BaseIntIdTable): IntEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt

    abstract fun toOutputDto(): OutputDto
}

val BaseIntEntity<*>.idValue: Int
    get() = this.id.value