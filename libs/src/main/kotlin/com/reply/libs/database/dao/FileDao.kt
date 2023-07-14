package com.reply.libs.database.dao

import com.reply.libs.config.database.BaseIntEntity
import com.reply.libs.config.database.BaseIntEntityClass
import com.reply.libs.database.models.FileModel
import org.jetbrains.exposed.dao.id.EntityID

class FileDao(id: EntityID<Int>): BaseIntEntity(id, FileModel) {
    companion object : BaseIntEntityClass<FileDao>(FileModel)

    var path by FileModel.path
}