package com.reply.tests.test

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.test.TestCreateDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime
import kotlin.test.assertEquals

class TestTestPatch : AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/test"

    private val test = runBlocking {
        val response = TestTestCreation.createTest(testClient, "New test", mutableListOf())
        response.body<TestOutputDto>()
    }

    @Test
    fun `Test success patch test`() {
        runBlocking {
            val newTestName = "${LocalDateTime.now()}"
            val response = testClient.patch("$globalEndpoint/${test.id}") {
                setBody<TestCreateDto>(
                    TestCreateDto(
                        title = "title",
                        type = 321,
                        formula = "$1 + $2",
                        metric = 1,
                        questions = mutableListOf()
                        )
                )
            }
            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<TestOutputDto>()
            }
            assertEquals(newTestName, body.title)
        }
    }

    @Test
    fun `Test failed on bad request`() {
        runBlocking {
            val response = testClient.patch("$globalEndpoint/${test.id}")
            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }
    }


    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val response = client.patch("$globalEndpoint/${test.id}")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed on trying to patch test which is not found`() {
        runBlocking {
            val response = testClient.patch("$globalEndpoint/0") {
                setBody<TestCreateDto>(
                    TestCreateDto(
                        title = "different title",
                        type = 321,
                        formula = "$1 + $2",
                        metric = 1,
                        questions = mutableListOf()
                    )
                )
            }
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }
}