package com.reply.libs.database.models

import com.reply.libs.config.database.BaseIntIdTable

object FileModel: BaseIntIdTable() {
    val path = text("path")
}