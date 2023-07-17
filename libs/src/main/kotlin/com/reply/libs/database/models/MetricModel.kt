package com.reply.libs.database.models

import com.reply.libs.utils.database.BaseIntIdTable

object MetricModel: BaseIntIdTable() {
    val name = varchar("name", 1024)
    val description = text("description")
    val deleted = bool("deleted")
}