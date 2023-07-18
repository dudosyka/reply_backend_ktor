package com.reply.file.service

import com.reply.libs.config.FileServiceConfig
import com.reply.libs.database.dao.FileDao
import com.reply.libs.database.models.FileModel
import com.reply.libs.dto.client.file.FileCreateDto
import com.reply.libs.dto.client.file.FileOutputDto
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.crud.asDto
import io.ktor.server.plugins.*
import io.ktor.util.date.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import kotlin.io.path.Path

class FileService(override val di: DI) : CrudService<FileOutputDto, FileCreateDto, FileDao>(di, FileModel, FileDao.Companion) {
    private fun generateUniqueName(fileName: String): String {
        val ext = fileName.split(".").last()

        return "${getTimeMillis()}.$ext"
    }
    fun remove(fileId: Int) = transaction {
        FileDao.findById(fileId)?.apply {
            val path = Path("${FileServiceConfig.savePath}/$path")
            path.toFile().delete()
        }?.delete() ?: throw NotFoundException()
    }

    fun create(fileCreateDto: FileCreateDto): FileOutputDto = transaction {
//        val bytes = Base64.getDecoder().decode(createFileDto.base64Encoded)
        val bytes = ByteArray(123)
        val fileName = generateUniqueName(fileCreateDto.fileName)
        val path = Path("${FileServiceConfig.savePath}/$fileName")

        path.toFile().writeBytes(bytes)

        insert(fileCreateDto) {
            this[FileModel.path] = fileName
        }.asDto()
    }
}