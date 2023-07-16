package com.reply.user.controller

import com.reply.libs.utils.kodein.KodeinController
import io.ktor.server.routing.*
import org.kodein.di.DI

class CompanyController(override val di: DI) : KodeinController() {
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {

    }
}