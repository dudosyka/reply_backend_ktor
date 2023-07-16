package com.reply.file.controller

import com.reply.file.service.FileService
import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.client.file.CreateFileDto
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.internal.exceptions.BadRequestException
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.utils.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

class FileController(override val di: DI) : KodeinController() {
    private val fileService: FileService by instance()
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        //Only internal requests can be used to send create request without authorization
        post("upload") {
            call.request.headers["Internal-Request"] ?: throw ForbiddenException()
            val createFileDto = call.receive<CreateFileDto>()

            val created = fileService.create(createFileDto)

            call.respond(created)
        }

        //Only internal requests can remove files without authorization (it used to rollback)
        delete("rollback/{fileId}") {
            call.request.headers["Internal-Request"] ?: throw ForbiddenException()
            val fileId = call.parameters["fileId"]?.toIntOrNull() ?: throw BadRequestException()

            fileService.remove(fileId)

            call.respond(SuccessOutputDto(msg = "File successfully removed"))
        }

        authenticate(RBACConfig.ADMIN.toString()) {
            route("closed") {
                post("upload") {
                    val createFileDto = call.receive<CreateFileDto>()
                    val created = fileService.create(createFileDto)

                    call.respond(created)
                }

                delete("delete/{fileId}") {
                    val fileId = call.parameters["fileId"]?.toIntOrNull() ?: throw BadRequestException()

                    fileService.remove(fileId)

                    call.respond(SuccessOutputDto(msg = "File successfully removed"))
                }
            }
        }
    }
}