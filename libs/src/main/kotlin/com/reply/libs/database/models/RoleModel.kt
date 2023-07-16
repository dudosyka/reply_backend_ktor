package com.reply.libs.database.models

import com.reply.libs.utils.database.BaseIntIdTable

object RoleModel: BaseIntIdTable() {
    val name = varchar("name", 256)
    val description = varchar("description", 2048).nullable().default(null)
}