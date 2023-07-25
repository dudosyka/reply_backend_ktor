package com.reply.libs.test

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.auth.AuthInputDto
import com.reply.libs.dto.client.auth.AuthOutputDto
import com.reply.libs.dto.client.auth.AuthorizedUserOutput
import com.reply.libs.dto.client.company.CompanyCreateDto
import com.reply.libs.dto.client.file.FileCreateDto
import com.reply.libs.dto.client.signup.SignUpInputClientDto
import com.reply.libs.dto.client.signup.SignUpInputDto
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import java.time.LocalDateTime

open class BaseTest {
    protected val openEndpoint = "http://localhost${ApiConfig.openEndpoint}/"
    protected val authorizedEndpoint = "http://localhost${ApiConfig.authorizedEndpoint}"
    protected val clientEndpoint = "http://localhost${ApiConfig.clientEndpoint}"
    protected val adminEndpoint = "http://localhost${ApiConfig.adminEndpoint}"

    protected open var client = HttpClient(Apache) {
        install(ContentNegotiation) {
            json()
        }
    }

    suspend fun signUpAdmin(login: String, email: String, password: String, companyName: String = "companyName") =
        client.post("${openEndpoint}auth/signup") {
            contentType(ContentType.Application.Json)
            setBody(
                SignUpInputDto(
                    login,
                    FileCreateDto(
                        "testFileName",
                        "testFile"
                    ),
                    password,
                    "FULLNAME",
                    "phone${LocalDateTime.now()}",
                    email,
                    CompanyCreateDto(
                        companyName,
                        FileCreateDto(
                            "name",
                            "test"
                        )
                    )
                )
            )
        }


    suspend fun signUpClient(
        login: String,
        email: String,
        password: String,
        companyId: Int
    ) = client.post("${openEndpoint}client/auth/signup") {
        setBody(
            SignUpInputClientDto(
                login,
                FileCreateDto(
                    "testFileName",
                    "testFile"
                ),
                password,
                "FULLNAME",
                "phone${LocalDateTime.now()}",
                email,
                companyId
            )
        )
    }

    suspend fun login(login: String, password: String) =
        client.post("${openEndpoint}auth") {
            contentType(ContentType.Application.Json)
            setBody(
                AuthInputDto(
                    login, password
                )
            )
        }

    suspend fun authorizeClient(login: String, password: String): HttpClient {
        val response = login(login, password)
        val responseBody = response.body<AuthOutputDto>()

        return HttpClient(Apache) {
            install(ContentNegotiation) {
                json()
            }
            headers {
                set("Authorization", "Bearer ${responseBody.token}")
                set("Content-Type", "application/json")
            }
        }
    }
    suspend fun newAdminRoleClient(
        login: String = "login${LocalDateTime.now()}",
        password: String = "hash",
        companyName: String = "companyName"
    ): HttpClient {
        signUpAdmin(login, login, password, companyName)
        return authorizeClient(login, password)
    }

    suspend fun newClientRoleClient(
        login: String = "login${LocalDateTime.now()}",
        password: String = "hash",
        companyId: Int
    ): HttpClient {
        signUpClient(login, login, password, companyId)
        return authorizeClient(login, password)
    }

    suspend fun getTokenData(
        client: HttpClient
    ): AuthorizedUserOutput {
        val response = client.get(authorizedEndpoint)
        return response.body<AuthorizedUserOutput>()
    }
}