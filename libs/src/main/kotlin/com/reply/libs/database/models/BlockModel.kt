package com.reply.libs.database.models

import com.reply.libs.utils.database.BaseIntIdTable

object BlockModel: BaseIntIdTable() {
    val name = text("name")
    val description = text("description")
    val time = integer("time")
    val week = integer("week").default(0)
    val company = reference("company", CompanyModel)
}