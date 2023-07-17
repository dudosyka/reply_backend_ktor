package com.reply.libs.database.dao

import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.database.models.FileModel
import com.reply.libs.dto.client.file.FileOutputDto
import com.reply.libs.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID

class FileDao(id: EntityID<Int>): BaseIntEntity<FileOutputDto>(id, FileModel) {
    companion object : BaseIntEntityClass<FileOutputDto, FileDao>(FileModel)

    var path by FileModel.path
    override fun toOutputDto(): FileOutputDto = FileOutputDto(
        idValue, path
    )
}