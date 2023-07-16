package com.reply.libs.utils.consul

import com.reply.libs.dto.client.base.OutputDto
import com.reply.libs.dto.internal.exceptions.ClientException
import com.reply.libs.dto.internal.exceptions.InternalServerError
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
import org.kodein.di.DIAware
import io.ktor.util.logging.*
import org.kodein.di.instance

abstract class ConsulClient(val serviceName: String): DIAware {
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
    val logger: Logger by instance()

    var exceptionBubbling: Boolean = true
    var internal: Boolean = false
    lateinit var envCall: ApplicationCall

    suspend inline fun <reified Output> deserializeResponse(response: HttpResponse): Output? {
        /*
            Suddenly we can get JsonConvertException because of error object in response,
            so we need to re-run deserialization process with appropriate serializable

            if after error deserialization we also got error throw it out to send 500 to the client
        */
        return try {
            response.body<Output>()
        } catch (e: JsonConvertException) {
            if (exceptionBubbling) {
                throw try {
                    response.body<ClientException>()
                } catch (e: JsonConvertException) {
                    InternalServerError("Failed to retrieve $serviceName")
                }
            } else {
                logger.error("Request to service '$serviceName' failed with $e")
                null
            }
        }
    }

    suspend fun <Output> withCall(call: ApplicationCall, block: suspend com.reply.libs.utils.consul.ConsulClient.() -> Output): Output {
        envCall = call
        return block()
    }

    suspend fun <Output> internal(block: suspend com.reply.libs.utils.consul.ConsulClient.() -> Output): Output {
        internal = true
        val result = block()
        internal = false
        return result
    }

    suspend fun <Output> noExceptionBubble(block: suspend com.reply.libs.utils.consul.ConsulClient.() -> Output): Output {
        exceptionBubbling = false
        val result = block()
        exceptionBubbling = true
        return result
    }

    fun manageRequest(call: ApplicationCall, internal: Boolean): HttpRequestBuilder.() -> Unit = {
        headers {
            appendAll(call.request.headers)
            if (internal) append("Internal-Request", "true")
        }
        contentType(ContentType.Application.Json)
    }

    suspend inline fun <reified Input : Any, reified Output : OutputDto> request(
        url: String,
        input: Input? = null,
        requestMethod: HttpMethod,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? {
        val body = input ?: if (requestMethod != HttpMethod.Get) envCall.receive<Input>() else null
        val response = client.request(url) {
            method = requestMethod
            this.apply(manageRequest(envCall, internal))
            if (requestMethod != HttpMethod.Get && body != null)
                setBody<Input>(body)
            this.apply(block)
        }

        return deserializeResponse(response)
    }

    suspend inline fun <reified Output: OutputDto> get(
        url: String,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? = request<Any, Output>(url, requestMethod = HttpMethod.Get, block = block)

    suspend inline fun <reified Input : Any, reified Output: OutputDto> post(
        url: String,
        input: Input? = null,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? = request<Input, Output>(url, input = input, requestMethod = HttpMethod.Post, block)

    suspend inline fun <reified Input : Any, reified Output: OutputDto> delete(
        url: String,
        input: Input? = null,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? = request<Input, Output>(url, input = input, requestMethod = HttpMethod.Delete, block)

    suspend inline fun <reified Input : Any, reified Output: OutputDto> patch(
        url: String,
        input: Input? = null,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? = request<Input, Output>(url, input = input, requestMethod = HttpMethod.Patch, block)
}