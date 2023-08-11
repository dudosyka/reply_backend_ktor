package com.reply.test.controller

import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.client.auth.AuthOutputDto
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.block.BlockCreateDto
import com.reply.libs.dto.client.block.BlockOnPassDto
import com.reply.libs.dto.client.block.BlockOutputDto
import com.reply.libs.dto.client.block.BlockTokenDto
import com.reply.libs.dto.internal.exceptions.BadRequestException
import com.reply.libs.utils.crud.asDto
import com.reply.libs.utils.kodein.KodeinController
import com.reply.test.service.BlockService
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
                    call.respond<BlockOutputDto>(blockService.create(createDto, getAuthorized(call)))
                }
                get {
                    call.respond<List<BlockOutputDto>>(blockService.getAll(getAuthorized(call)))
                }
                get("{id}") {
                    val blockId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                    call.respond<BlockOutputDto>(blockService.getOne(blockId, getAuthorized(call)))
                }
                patch("{id}") {
                    val blockId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                    val updateDto = call.receive<BlockCreateDto>()
                    val result = blockService.patch(updateDto, blockId, getAuthorized(call))
                    call.respond<BlockOutputDto>(result.asDto())
                }
                delete("{id}") {
                    val blockId = call.parameters["id"]?.toIntOrNull() ?: throw BadRequestException()
                    val authorizedUser = getAuthorized(call)
                    call.respond<SuccessOutputDto>(blockService.delete(blockId, authorizedUser))
                }
                post("token") {
                    val blockTokenDto = call.receive<BlockTokenDto>()
                    call.respond<AuthOutputDto>(blockService.getToken(blockTokenDto, call, getAuthorized(call)))
                }
                get("company/{companyId}"){
                    val companyId = call.parameters["companyId"]?.toIntOrNull() ?: throw BadRequestException()
                    call.respond<List<BlockOutputDto>>(blockService.getAllByCompany(companyId))
                }
            }
        }
        authenticate(RBACConfig.AUTHORIZED.toString()) {
            get("block/pass") {
                call.respond<BlockOnPassDto>(blockService.getOnPass(getAuthorized(call)))
            }
        }
    }
}