package com.reply.libs.config.database

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

abstract class BaseIntIdTable: IntIdTable() {
    val createdAt = datetime("createdAt").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updatedAt").nullable()
}