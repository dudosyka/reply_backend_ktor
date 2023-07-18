package com.reply.libs.database.models

import com.reply.libs.utils.database.BaseIntIdTable

object GroupModel: BaseIntIdTable() {
    val name = varchar("name", 255)
    val company = reference("company", CompanyModel)
}