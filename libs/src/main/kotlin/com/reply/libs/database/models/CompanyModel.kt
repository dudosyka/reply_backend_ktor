package com.reply.libs.database.models

import com.reply.libs.utils.database.BaseIntIdTable

object CompanyModel: BaseIntIdTable() {
    val name = varchar("name", 256)
    val logo = reference("logo", FileModel)
}