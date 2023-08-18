package com.reply.libs.database.models.result

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

object BlockTestResultModel: Table() {
    val blockResult = reference("block_result", BlockResultModel, ReferenceOption.CASCADE, ReferenceOption.CASCADE)
    val testResult = reference("test_result", TestResultModel, ReferenceOption.CASCADE, ReferenceOption.CASCADE)

    val createdAt = datetime("createdAt").clientDefault { LocalDateTime.now() }
    val updatedAt = datetime("updatedAt").nullable()
    override val primaryKey = PrimaryKey(blockResult, testResult)
}