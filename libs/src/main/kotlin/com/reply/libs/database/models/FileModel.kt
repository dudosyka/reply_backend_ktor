package com.reply.libs.database.models

import com.reply.libs.utils.database.BaseIntIdTable

object FileModel: BaseIntIdTable() {
    var path = text("path")
}