package com.reply.file.controller

import com.reply.file.service.FileService
import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.client.file.FileCreateDto
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.file.FileDataDto
import com.reply.libs.dto.client.file.FileOutputDto
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
import java.io.File

class FileController(override val di: DI) : KodeinController() {
    private val fileService: FileService by instance()
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        //Only internal requests can be used to send create request without authorization
        post("upload") {
            call.request.headers["Internal-Request"] ?: throw ForbiddenException()
            val fileCreateDto = call.receive<FileCreateDto>()

            val created = fileService.create(fileCreateDto)

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
                    val fileCreateDto = call.receive<FileCreateDto>()
                    val created = fileService.create(fileCreateDto)

                    call.respond(created)
                }

                delete("delete/{fileId}") {
                    val fileId = call.parameters["fileId"]?.toIntOrNull() ?: throw BadRequestException()

                    fileService.remove(fileId)

                    call.respond(SuccessOutputDto(msg = "File successfully removed"))
                }
            }
            route("file"){
                get("link/{fileId}"){
                    val fileId = call.parameters["fileId"]?.toIntOrNull() ?: throw BadRequestException()
                    call.respond<FileOutputDto>(fileService.getLink(fileId))
                }
            }
//            route("link"){
//                get("file/{fileName}"){
//                    val fileName = call.parameters["fileName"]?: throw BadRequestException()
//                    val path = "C://Users//Sasha//IdeaProjects//reply_backend_ktor//files//${fileName}"
//                    call.respond(FileDataDto(
//                        File(path)
//                    ))
//                }
//            }
        }
    }
}