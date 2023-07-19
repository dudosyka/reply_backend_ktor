package com.reply.block.controller

import com.reply.block.service.BlockService
import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.block.BlockCreateDto
import com.reply.libs.dto.client.block.BlockOutputDto
import com.reply.libs.dto.internal.exceptions.BadRequestException
import com.reply.libs.dto.internal.exceptions.InternalServerError
import com.reply.libs.utils.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

class BlockController(override val di : DI) : KodeinController() {
    private val blockService : BlockService by instance()
    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.ADMIN.toString()) {
            //CRUD Endpoints
            route("block") {
                post {
                    val createDto = call.receive<BlockCreateDto>()
                    call.respond<BlockOutputDto>(blockService.create(createDto, getAuthorized(call),  call))
                }
                get {
                    call.respond<List<BlockOutputDto>>(blockService.getAll(getAuthorized(call)))
                }
                get("{id}") {
                    val blockId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                    call.respond<BlockOutputDto>(blockService.getOne(call, blockId, getAuthorized(call)))
                }
                patch("{id}") {
                    val blockId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                    val updateDto = call.receive<BlockCreateDto>()
                    val result = blockService.patch(updateDto, blockId, getAuthorized(call), call)
                    if (result)
                        call.respond<SuccessOutputDto>(SuccessOutputDto("success", "Block successfully updated"))
                    else
                        throw InternalServerError("Failed to update block with id = $blockId")
                }
                delete("{id}") {
                    val blockId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                    blockService.delete(blockId, getAuthorized(call))
                    call.respond<SuccessOutputDto>(SuccessOutputDto("success", "Block successfully removed"))
                }
            }
        }
    }
}