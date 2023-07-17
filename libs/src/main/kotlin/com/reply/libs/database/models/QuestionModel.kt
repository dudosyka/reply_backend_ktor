package com.reply.libs.database.models

import com.reply.libs.utils.database.BaseIntIdTable
import org.jetbrains.exposed.sql.ReferenceOption

object QuestionModel: BaseIntIdTable() {
    val title = varchar("title", 1024)
    val type = reference("type", QuestionTypeModel)
    val test = reference("test", TestModel, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val relative_id = integer("relative_id")
    val value = text("value")
    val coins = integer("coins")
    val picture = reference("picture", FileModel).nullable().default(null)
}