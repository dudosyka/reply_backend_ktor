package com.reply.libs.utils.consul

import com.reply.libs.config.ApiConfig
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
import io.ktor.util.*
import org.kodein.di.DIAware
import io.ktor.util.logging.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
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

    var ignoreResult: Boolean = false
    var internal: Boolean = false
    lateinit var envCall: ApplicationCall

    //Current call request uri
    val curUri: String
        get() = envCall.request.uri
                    .removePrefix(ApiConfig.adminEndpoint)
                    .removePrefix(ApiConfig.clientEndpoint)
                    .removePrefix(ApiConfig.openEndpoint)
                    .removePrefix(ApiConfig.authorizedEndpoint)
                    .removePrefix(ApiConfig.mainEndpoint)

    suspend inline fun <reified Output> deserializeResponse(response: HttpResponse): Output? {
        /*
            Suddenly we can get JsonConvertException because of error object in response,
            so we need to re-run deserialization process with appropriate serializable

            if after error deserialization we also got error throw it out to send 500 to the client
        */
        return if (ignoreResult) {
            val result = response.bodyAsText()
            logger.info("Request with ignore result received: $result")
            null
        } else try {
            response.body<Output>()
        } catch (e: JsonConvertException) {
            throw try {
                response.body<ClientException>()
            } catch (e: JsonConvertException) {
                InternalServerError("Failed to retrieve $serviceName")
            }
        }
    }

    suspend fun <Output> withCall(call: ApplicationCall, block: suspend com.reply.libs.utils.consul.ConsulClient.() -> Output): Output {
        envCall = call
        return try { block() } catch (e: Exception) { logger.info("With call exception $e"); throw e }
    }

    suspend fun <Output> internal(block: suspend com.reply.libs.utils.consul.ConsulClient.() -> Output): Output {
        internal = true
        val result = try { block() } catch (e: Exception) { logger.info("Internal exception $e"); throw e }
        internal = false
        return result
    }

    suspend fun <Output> ignoreResult(block: suspend com.reply.libs.utils.consul.ConsulClient.() -> Output): Output {
        ignoreResult = true
        val result = try { block() } catch (e: Exception) { logger.info("Ignore result exception $e"); throw e }
        ignoreResult = false
        return result
    }

    fun manageRequest(call: ApplicationCall, internal: Boolean): HttpRequestBuilder.() -> Unit = {
        headers {
            appendAll(call.request.headers)
            if (internal) append("Internal-Request", "true")
        }
        contentType(ContentType.Application.Json)
    }

    @OptIn(ExperimentalSerializationApi::class)
    suspend fun getFile(url: String = curUri): ByteArray {
        val response = client.request(url) {
            method = HttpMethod.Get
            this.apply(manageRequest(envCall, internal))
        }

        val bytes = response.bodyAsChannel().toByteArray()
        val error = try {
           Json.decodeFromStream<ClientException>(bytes.inputStream())
        } catch (_: Exception) { null }

        if (error != null)
            throw error

        return bytes
    }

    suspend inline fun <reified Input : Any, reified Output> request(
        url: String,
        input: Input? = null,
        requestMethod: HttpMethod,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? {
        var body: Input? = null
        if (input !is EmptyBody)
            body = input ?: if (requestMethod != HttpMethod.Get) envCall.receive<Input>() else null
        val response = client.request(url) {
            method = requestMethod
            this.apply(manageRequest(envCall, internal))
            if (requestMethod != HttpMethod.Get && body != null)
                setBody<Input>(body)
            this.apply(block)
        }

        return deserializeResponse(response)
    }

    suspend inline fun <reified Output> get(
    url: String,
    noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? = request<Any, Output>(url, requestMethod = HttpMethod.Get, block = block)
    suspend inline fun <reified Output> get(
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? = request<Any, Output>(curUri, requestMethod = HttpMethod.Get, block = block)

    suspend inline fun <reified Input : Any, reified Output> post(
        url: String,
        input: Input? = null,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? = request<Input, Output>(url, input = input, requestMethod = HttpMethod.Post, block)

    suspend inline fun <reified Input : Any, reified Output> post(
        input: Input? = null,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? = request<Input, Output>(curUri, input = input, requestMethod = HttpMethod.Post, block)

    suspend inline fun <reified Input : Any, reified Output> delete(
        url: String,
        input: Input? = null,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? = request<Input, Output>(url, input = input, requestMethod = HttpMethod.Delete, block)

    suspend inline fun <reified Input : Any, reified Output> delete(
        input: Input? = null,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? = request<Input, Output>(curUri, input = input, requestMethod = HttpMethod.Delete, block)

    suspend inline fun <reified Input : Any, reified Output> patch(
        url: String,
        input: Input? = null,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? = request<Input, Output>(url, input = input, requestMethod = HttpMethod.Patch, block)

    suspend inline fun <reified Input : Any, reified Output> patch(
        input: Input? = null,
        noinline block: HttpRequestBuilder.() -> Unit = {}
    ): Output? = request<Input, Output>(curUri, input = input, requestMethod = HttpMethod.Patch, block)
}