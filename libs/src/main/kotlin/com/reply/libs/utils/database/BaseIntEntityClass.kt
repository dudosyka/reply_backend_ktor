package com.reply.libs.utils.database

import org.jetbrains.exposed.dao.EntityChangeType
import org.jetbrains.exposed.dao.EntityHook
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.toEntity
import org.jetbrains.exposed.sql.Query
import java.time.LocalDateTime

abstract class BaseIntEntityClass<Output, E : BaseIntEntity<Output>>(table: BaseIntIdTable) : IntEntityClass<E>(table) {
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                try {
                    action.toEntity(this)?.updatedAt = LocalDateTime.now()
                } catch (_: Exception) { }
            }
        }
    }

    fun wrapQuery(query: Query): List<Output> =
        wrapRows(query).map { it.toOutputDto() }
}