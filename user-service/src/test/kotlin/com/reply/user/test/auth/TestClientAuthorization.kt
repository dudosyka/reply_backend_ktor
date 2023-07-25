package com.reply.user.test.auth

import com.reply.libs.dto.client.auth.AuthOutputDto
import com.reply.libs.dto.client.auth.AuthorizedUserOutput
import com.reply.libs.dto.client.base.SuccessOutputDto
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

class TestClientAuthorization : BaseTest() {

    private val login: String = "testLogin${LocalDateTime.now()}"
    private val password: String = "hash"
    private val companyId: Int = 1

    @Test
    fun `Test bad request`() {
        runBlocking {
            val signUpResponse = this@TestClientAuthorization.client.post("${openEndpoint}client/auth/signup") {}

            assertEquals(HttpStatusCode.UnsupportedMediaType, signUpResponse.status)
        }
    }

    @Test
    fun `Test success authorization flow (signup + login + check token)`() {
        runBlocking {
            //Check signup
            val signUpResponse = signUpClient(login, login, password, companyId)

            assertEquals(HttpStatusCode.OK, signUpResponse.status)

            val signUpBody = assertDoesNotThrow {
                signUpResponse.body<SuccessOutputDto>()
            }

            assertEquals("success", signUpBody.status)
            assertEquals("Successfully signup", signUpBody.msg)



            //Check auth
            val authResponse = login(login, password)

            assertEquals(HttpStatusCode.OK, authResponse.status)

            val authBody = assertDoesNotThrow {
                authResponse.body<AuthOutputDto>()
            }



            val checkTokenResponse = this@TestClientAuthorization.client.get(authorizedEndpoint) {
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
            signUpClient(login, email, password, companyId)

            //Run signup with same credentials and wait for exception
            val failedSignUpResponse = signUpClient(login, email, password, companyId)

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

    @Test
    fun `Test company not found on signUp with bad company ID`() {
        runBlocking {
            val response = signUpClient(login, login, password, 0)

            assertEquals(HttpStatusCode.NotFound, response.status)

            val responseBody = assertDoesNotThrow {
                response.body<ClientException>()
            }

            assertEquals(404, responseBody.status)
        }
    }
}