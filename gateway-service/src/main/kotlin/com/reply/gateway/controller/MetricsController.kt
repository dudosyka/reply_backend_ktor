package com.reply.gateway.controller

import com.reply.gateway.consul.TestClient
import com.reply.gateway.consul.UserClient
import com.reply.libs.config.ApiConfig
import com.reply.libs.consul.FileServiceClient
import com.reply.libs.utils.kodein.KodeinController
import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.micrometer.prometheus.PrometheusConfig
import io.micrometer.prometheus.PrometheusMeterRegistry
import org.kodein.di.DI
import org.kodein.di.instance

class MetricsController(override val di: DI) : KodeinController() {
    private val testClient: TestClient by instance()
    private val userClient: UserClient by instance()
    private val fileClient: FileServiceClient by instance()
    private val appMicrometerRegistry = PrometheusMeterRegistry(PrometheusConfig.DEFAULT)
    /**
     * Method that subtypes must override to register the handled [Routing] routes.
     */
    override fun Routing.registerRoutes() {
        route("/api/metrics") {
            get("{service}") {
                val service = call.parameters["service"] ?: throw NotFoundException()
                when (service) {
                    "gateway" -> call.respond(appMicrometerRegistry.scrape())
                    "user" -> call.respond(userClient.withCall(call) { proxy("${ApiConfig.openEndpoint}/metrics") })
                    "test" -> call.respond(testClient.withCall(call) { proxy("${ApiConfig.openEndpoint}/metrics") })
                    "file" -> call.respond(fileClient.withCall(call) { proxy("${ApiConfig.openEndpoint}/metrics") })
                }
            }
        }
    }
}