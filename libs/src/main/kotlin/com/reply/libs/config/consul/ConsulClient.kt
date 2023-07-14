package com.reply.libs.config.consul

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.exceptions.ClientException
import com.reply.libs.dto.exceptions.InternalServerError
import com.reply.libs.plugins.consul.ConsulClient
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.serialization.kotlinx.json.*
import io.ktor.server.application.*
import io.ktor.server.request.*

abstract class ConsulClient(val serviceName: String) {
    var client: HttpClient = HttpClient(Apache) {
        install(ConsulClient) {
            consulUrl = "http://localhost:8500"
            serviceName = this@ConsulClient.serviceName
            loadBalancer(roundRobin())
        }
        install(ContentNegotiation) {
            json()
        }
    }

    suspend inline fun <reified Output> deserializeAnswer(response: HttpResponse): Output {
        /*
            Suddenly we can get JsonConvertException because of error object in response,
            so we need to re-run deserialization process with appropriate serializable

            if after error deserialization we also got error throw it out to send 500 to the client
        */
        return try {
            response.body<Output>()
        } catch (e: JsonConvertException) {
            throw try {
                response.body<ClientException>()
            } catch (e: JsonConvertException) {
                InternalServerError("Failed to retrieve $serviceName")
            }
        }
    }

    suspend inline fun <reified Output> get(
        url: String,
        call: ApplicationCall,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output = deserializeAnswer(
        client.get(
            "${ApiConfig().protocol}://$serviceName/$url"
        ) {
            headers {
                appendAll(call.request.headers)
            }
            contentType(ContentType.Application.Json)
            this.apply(block)
        }
    )


    suspend inline fun <reified Input : Any, reified Output> post(
        url: String,
        call: ApplicationCall,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output {
        val body = call.receive<Input>()
        val response = client.post(
            "${ApiConfig().protocol}://$serviceName/$url"
        ) {
            headers {
                appendAll(call.request.headers)
            }
            contentType(ContentType.Application.Json)
            setBody<Input>(body)
            this.apply(block)
        }

        return deserializeAnswer(response)
    }
}