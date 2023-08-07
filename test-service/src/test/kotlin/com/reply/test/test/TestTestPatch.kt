package com.reply.test.test

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.test.TestCreateDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime
import kotlin.test.assertEquals

class TestTestPatch : AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val secondAdmin = runBlocking {
        newAdminRoleClient()
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/test"

    private suspend fun createNewTest(client: HttpClient, data: TestCreateDto = TestCreateDto(
        title = "testTitle",
        type = 2,
        formula = "$1 + $2",
        metric = 1,
        questions = mutableListOf()
    ), newName: String = "testTitle") = run {
        data.title = newName
        val create = client.post(globalEndpoint) {
            setBody<TestCreateDto>(
                data
            )
        }
        create.body<TestOutputDto>()
    }

    @Test
    fun `Test success patch test`() {
        runBlocking {
            val newTestName = "${LocalDateTime.now()}"
            // Delays are need to bypass sync errors
            // when new user created time to time we can get error that company doesn't exist
            // when create new test because of synchronization
            delay(1000)
            val test = createNewTest(testClient, newName = "newTestName")
            delay(1000)
            val response = testClient.patch("$globalEndpoint/${test.id}") {
                setBody<TestCreateDto>(
                    TestCreateDto(
                        title = newTestName,
                        type = 1,
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
            val test = createNewTest(testClient)
            val response = testClient.patch("$globalEndpoint/${test.id}")
            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }
    }


    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val test = createNewTest(testClient)
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

    @Test
    fun `Test failed on try to delete built-in test`() {
        runBlocking {
            val response = testClient.patch("$globalEndpoint/1") {
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
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun `Test failed on try delete test from different company`() {
        runBlocking {
            //Firstly create test with testClient
            val test = createNewTest(testClient)

            //Then try to delete created test with second admin
            val response = secondAdmin.patch("$globalEndpoint/${test.id}") {
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

            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }
}