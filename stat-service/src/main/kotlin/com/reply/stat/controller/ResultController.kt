package com.reply.stat.controller

import com.reply.libs.config.RBACConfig
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.result.BlockResultCreateDto
import com.reply.libs.dto.internal.exceptions.InternalServerError
import com.reply.libs.utils.kodein.KodeinController
import com.reply.stat.service.ResultService
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.kodein.di.DI
import org.kodein.di.instance

class ResultController(override val di: DI) : KodeinController() {
    private val resultService: ResultService by instance()
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        authenticate(RBACConfig.AUTHORIZED.toString()) {
            post("block/pass") {
                val data = call.receive<BlockResultCreateDto>()
                val result = resultService.passBlock(data, getAuthorized(call))
                if (result)
                    call.respond(SuccessOutputDto(msg = "Result saved successfully"))
                else
                    throw InternalServerError("Result saving failed")
            }
        }
    }
}