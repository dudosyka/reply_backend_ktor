package com.reply.libs.consul

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.file.FileCreateDto
import com.reply.libs.dto.client.file.FileOutputDto
import com.reply.libs.utils.consul.ConsulClient
import com.reply.libs.utils.consul.EmptyBody
import io.ktor.server.application.*
import org.kodein.di.DI


class FileServiceClient(override val di: DI) : ConsulClient(ApiConfig.fileServiceName) {
    suspend fun uploadFile(call: ApplicationCall, file: FileCreateDto) =
        withCall(call) {
            internal {
                post<FileCreateDto, FileOutputDto>("upload", input = file)!!
            }
        }

    suspend fun rollbackUploading(call: ApplicationCall, file: Int) =
        withCall(call) {
            internal {
                ignoreResult {
                    delete<EmptyBody, SuccessOutputDto>("rollback/$file", input = EmptyBody)
                }
            }
        }
}