package com.reply.libs.consul.client

import com.reply.libs.config.ApiConfig
import com.reply.libs.consul.server.roundRobin
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.request.*

abstract class ConsulClient(val serviceName: String) {
    var client: HttpClient = HttpClient(Apache) {
        install(ConsulClientFeature) {
            consulUrl = "http://localhost:8500"
            serviceName = this@ConsulClient.serviceName
            loadBalancer(roundRobin())
        }
        install(ContentNegotiation) {
            json()
        }
    }

    suspend inline fun <reified OutputDto> get(
        url: String,
        call: ApplicationCall,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ) =
        client.get(
            "${ApiConfig().protocol}://$serviceName/$url"
        ) {
            headers {
                appendAll(call.request.headers)
            }
            contentType(ContentType.Application.Json)
            this.apply(block)
        }.body<OutputDto>()

    suspend inline fun <reified InputDto : Any, reified OutputDto> post(
        url: String,
        call: ApplicationCall,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): OutputDto {
        val body = call.receive<InputDto>()
        return client.post(
            "${ApiConfig().protocol}://$serviceName/$url"
        ) {
            headers {
                appendAll(call.request.headers)
            }
            contentType(ContentType.Application.Json)
            setBody<InputDto>(body)
            this.apply(block)
        }.body<OutputDto>()
    }
}