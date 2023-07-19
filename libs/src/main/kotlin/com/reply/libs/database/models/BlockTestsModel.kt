package com.reply.libs.database.models

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object BlockTestsModel : Table() {
    val block = reference("block", BlockModel, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val test = reference("test", TestModel, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    override val primaryKey = PrimaryKey(block, test)
}