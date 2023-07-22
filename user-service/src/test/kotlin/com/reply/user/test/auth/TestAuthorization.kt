package com.reply.user.test.auth

import com.reply.libs.dto.client.auth.AuthInputDto
import com.reply.libs.dto.client.auth.AuthOutputDto
import com.reply.libs.dto.client.auth.AuthorizedUserOutput
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.company.CompanyCreateDto
import com.reply.libs.dto.client.file.FileCreateDto
import com.reply.libs.dto.client.signup.SignUpInputDto
import com.reply.libs.dto.internal.exceptions.ClientException
import com.reply.libs.dto.internal.exceptions.DuplicateEntryException
import com.reply.libs.test.BaseTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class TestAuthorization : BaseTest() {

    private val login: String = "testLogin${LocalDateTime.now()}"
    private val password: String = "hash"

    @Test
    fun `Test bad request`() {
        runBlocking {
            val signUpResponse = this@TestAuthorization.client.post("${openEndpoint}auth/signup") {}

            assertEquals(HttpStatusCode.UnsupportedMediaType, signUpResponse.status)
        }
    }

    @Test
    fun `Test success authorization flow (signup + login + check token)`() {
        runBlocking {
            //Check signup
            val signUpResponse = this@TestAuthorization.client.post("${openEndpoint}auth/signup") {
                contentType(ContentType.Application.Json)
                setBody(SignUpInputDto(
                    login,
                    FileCreateDto(
                        "testFileName",
                        "testFile"
                    ),
                    password,
                    "FULLNAME",
                    "phone${LocalDateTime.now()}",
                    "email${LocalDateTime.now()}",
                    CompanyCreateDto(
                        "NEW_COMPANY",
                        FileCreateDto(
                            "name",
                            "test"
                        )
                    )
                ))
            }

            assertEquals(HttpStatusCode.OK, signUpResponse.status)

            val signUpBody = assertDoesNotThrow {
                signUpResponse.body<SuccessOutputDto>()
            }

            assertEquals("success", signUpBody.status)
            assertEquals("Successfully signup", signUpBody.msg)



            //Check auth
            val authResponse = this@TestAuthorization.client.post("${openEndpoint}auth") {
                contentType(ContentType.Application.Json)
                setBody(AuthInputDto(
                    login, password
                ))
            }
            assertEquals(HttpStatusCode.OK, authResponse.status)

            val authBody = assertDoesNotThrow {
                authResponse.body<AuthOutputDto>()
            }



            val checkTokenResponse = this@TestAuthorization.client.get(authorizedEndpoint) {
                contentType(ContentType.Application.Json)
                headers {
                    set("Authorization", "Bearer ${authBody.token}")
                }
            }

            //Check token is valid
            val checkTokenBody = assertDoesNotThrow {
                checkTokenResponse.body<AuthorizedUserOutput>()
            }

            assertEquals(HttpStatusCode.OK, checkTokenResponse.status)
            assertEquals(login, checkTokenBody.login)
        }
    }

    @Test
    fun `Test failed signup on not unique login or email`() {
        runBlocking {
            val login = "Login${LocalDateTime.now()}"
            val email = "Login${LocalDateTime.now()}"

            //Firstly run signup
            this@TestAuthorization.client.post("${openEndpoint}auth/signup") {
                contentType(ContentType.Application.Json)
                setBody(SignUpInputDto(
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
                        "NEW_COMPANY",
                        FileCreateDto(
                            "name",
                            "test"
                        )
                    )
                ))
            }

            //Run signup with same credentials wait for exception
            val failedSignUpResponse = this@TestAuthorization.client.post("${openEndpoint}auth/signup") {
                contentType(ContentType.Application.Json)
                setBody(SignUpInputDto(
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
                        "NEW_COMPANY",
                        FileCreateDto(
                            "name",
                            "test"
                        )
                    )
                ))
            }
            val exception = DuplicateEntryException()
            assertEquals(HttpStatusCode(exception.status, exception.statusDescription), failedSignUpResponse.status)

            val signUpBody = assertDoesNotThrow {
                failedSignUpResponse.body<ClientException>()
            }

            assertEquals(409, signUpBody.status)
            assertEquals("Duplicate entry", signUpBody.statusDescription)
            assertEquals("Login and Email must be unique", signUpBody.msg)
        }
    }
}