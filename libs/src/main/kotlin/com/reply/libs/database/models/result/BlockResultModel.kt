package com.reply.libs.database.models.result

import com.reply.libs.database.models.BlockModel
import com.reply.libs.database.models.CompanyModel
import com.reply.libs.database.models.UserModel
import com.reply.libs.utils.database.BaseIntIdTable

object BlockResultModel: BaseIntIdTable() {
    val time = integer("time")
    val week = integer("week")
    val blockTitle = text("block_title")
    val companyTitle = text("company_title")
    val isValid = bool("is_valid")
    val user = reference("user", UserModel)
    val block = reference("block", BlockModel).nullable().default(null)
    val company = reference("company", CompanyModel).nullable().default(null)
}