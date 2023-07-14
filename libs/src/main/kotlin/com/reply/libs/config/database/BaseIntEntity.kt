package com.reply.libs.config.database

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.id.EntityID

abstract class BaseIntEntity(id: EntityID<Int>, table: BaseIntIdTable): IntEntity(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

val BaseIntEntity.idValue: Int
    get() = this.id.value