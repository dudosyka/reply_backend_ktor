package com.reply.libs.database.models

import com.reply.libs.utils.database.BaseIntIdTable

object TestModel: BaseIntIdTable() {
    val title = varchar("title", 1024)
    val type = reference("type", QuestionTypeModel)
    //Can be null due to 'built-in' tests which are not editable and provided for all companies
    val company = reference("company", CompanyModel).nullable().default(null)
    val formula = text("formula")
    //Can be null by the same reason as company
    val author = reference("author", UserModel).nullable().default(null)
    val metric = reference("metric", MetricModel)
}