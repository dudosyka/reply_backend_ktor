package com.reply.libs.database.models

import com.reply.libs.utils.database.BaseIntIdTable

object QuestionTypeModel: BaseIntIdTable() {
    val name = varchar("name", 1024)
}