package com.reply.libs.database.models.result

import com.reply.libs.database.models.MetricModel
import com.reply.libs.database.models.TestModel
import com.reply.libs.utils.database.BaseIntIdTable

object TestResultModel: BaseIntIdTable() {
    val test = reference("test", TestModel).nullable().default(null)
    val metric = reference("metric", MetricModel)
    val testTitle = text("test_title")
        //(\(((([$&]\d+)|(\((([$&]\d+)(.)([$&]\d+))\)))(.)(([$&]\d+)|(\((([$&]\d+)(.)([$&]\d+))\))))\))
    val value = integer("value")
}