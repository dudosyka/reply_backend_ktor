package com.reply.file.service

import com.reply.libs.config.FileServiceConfig
import com.reply.libs.database.dao.FileDao
import com.reply.libs.dto.client.file.FileCreateDto
import com.reply.libs.dto.client.file.FileDto
import com.reply.libs.utils.database.idValue
import io.ktor.server.plugins.*
import io.ktor.util.date.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.DIAware
import kotlin.io.path.Path

class FileService(override val di: DI) : DIAware {
    private fun generateUniqueName(fileName: String): String {
        val ext = fileName.split(".").last()

        return "${getTimeMillis()}.$ext"
    }
    fun remove(fileId: Int) = transaction {
        FileDao.findById(fileId)?.apply {
            val path = Path("${FileServiceConfig().savePath}/$path")
            path.toFile().delete()
        }?.delete() ?: throw NotFoundException()
    }

    fun create(fileCreateDto: FileCreateDto): FileDto = transaction {
//        val bytes = Base64.getDecoder().decode(createFileDto.base64Encoded)
        val bytes = ByteArray(123)
        val fileName = generateUniqueName(fileCreateDto.fileName)
        val path = Path("${FileServiceConfig().savePath}/$fileName")

        path.toFile().writeBytes(bytes)

        val created = FileDao.new {
            this.path = fileName
        }

        FileDto(
            id = created.idValue,
            path = fileName
        )
    }
}