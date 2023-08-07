package com.reply.file.service

import com.reply.libs.config.FileServiceConfig
import com.reply.libs.database.dao.FileDao
import com.reply.libs.database.models.FileModel
import com.reply.libs.dto.client.file.FileCreateDto
import com.reply.libs.dto.client.file.FileOutputDto
import com.reply.libs.dto.internal.exceptions.InternalServerError
import com.reply.libs.dto.internal.exceptions.ModelNotFound
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.crud.asDto
import io.ktor.util.date.*
import org.kodein.di.DI
import java.io.File
import java.io.IOException
import kotlin.io.path.Path
import kotlin.io.path.exists

class FileService(override val di: DI) : CrudService<FileOutputDto, FileCreateDto, FileDao>(di, FileModel, FileDao.Companion) {
    private fun generateUniqueName(fileName: String): String {
        val ext = fileName.split(".").last()

        return "${getTimeMillis()}.$ext"
    }
    suspend fun remove(fileId: Int) = transaction {
        (FileDao.findById(fileId)?.apply {
            val path = Path("${FileServiceConfig.savePath}/${path}")
            if (path.exists())
                Path("${FileServiceConfig.savePath}/${path}").toFile().delete()
            else
                throw ModelNotFound()
        } ?: throw ModelNotFound()).delete()
        commit()
    }

    suspend fun create(fileCreateDto: FileCreateDto): FileOutputDto = transaction {
//        val bytes = Base64.getDecoder().decode(createFileDto.base64Encoded)
        val bytes = ByteArray(123)
        val fileName = generateUniqueName(fileCreateDto.fileName)
        val path = Path("${FileServiceConfig.savePath}$fileName")

        path.toFile().writeBytes(bytes)

        val result = insert(fileCreateDto) {
            this[FileModel.path] = fileName
        }
        commit()
        result.asDto()
    }

    //This method returns link on the file by file_id
    suspend fun getLink(fileId : Int): File = transaction{
        try {
            File("${FileServiceConfig.savePath}${FileDao.findById(fileId)?.path ?: throw ModelNotFound("File with id $fileId not found") }")
        } catch (e: IOException) {
            throw InternalServerError("File read error")
        }
    }
}