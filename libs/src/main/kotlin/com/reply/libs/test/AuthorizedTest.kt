package com.reply.libs.test

import com.reply.libs.config.ApiConfig
import com.reply.libs.config.RBACConfig
import com.reply.libs.plugins.createToken
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*

open class AuthorizedTest: BaseTest() {
    override var client = HttpClient(Apache) {
        install(ContentNegotiation) {
            json()
        }
    }

    protected var authorizedRoleClient: HttpClient
    protected var clientRoleClient: HttpClient
    protected var adminRoleClient: HttpClient

    init {
        val adminToken = createToken(
            mutableMapOf(
                "id" to "1",
                "login" to "testAdmin",
                "role" to RBACConfig.ADMIN.toString(),
                "companyId" to "1"
            )
        )
        val clientToken = createToken(
            mutableMapOf(
                "id" to "2",
                "login" to "testClient",
                "role" to RBACConfig.CLIENT.toString(),
                "companyId" to "1"
            )
        )
        authorizedRoleClient = HttpClient(Apache) {
            install(ContentNegotiation) {
                json()
            }
            headers {
                set("Authorization", "Bearer $adminToken")
            }
        }
        clientRoleClient = HttpClient(Apache) {
            install(ContentNegotiation) {
                json()
            }
            headers {
                set("Authorization", "Bearer $clientToken")
            }
        }
        adminRoleClient = HttpClient(Apache) {
            install(ContentNegotiation) {
                json()
            }
            headers {
                set("Authorization", "Bearer $adminToken")
            }
        }
    }
}