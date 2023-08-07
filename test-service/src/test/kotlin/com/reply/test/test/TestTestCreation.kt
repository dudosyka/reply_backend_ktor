package com.reply.test.test

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.test.TestCreateDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals
import kotlin.test.Test

class TestTestCreation: AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/test"
    private fun createTest(
        client: HttpClient,
        dto: TestCreateDto = TestCreateDto(
            title = "title",
            type = 2,
            formula = "$1 + $2",
            metric = 1,
            questions = mutableListOf()
        )
    ) = runBlocking {
        client.post(globalEndpoint) {
            setBody<TestCreateDto>(
                dto
            )
        }
    }

    @Test
    fun `Test success test creation`() {
        runBlocking {
            val testTitle = "Test title"
            val response = createTest(testClient, TestCreateDto(
                title = testTitle,
                type = 2,
                formula = "$1 + $2",
                metric = 1,
                questions = mutableListOf()
            ))

            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<TestOutputDto>()
            }
            assertEquals(testTitle, body.title)
        }
    }

    @Test
    fun `Test failed on provide question_type id which is not exists`() {
        runBlocking {
            val testTitle = "Test title"
            val response = createTest(testClient, TestCreateDto(
                title = testTitle,
                type = 0,
                formula = "$1 + $2",
                metric = 1,
                questions = mutableListOf()
            ))

            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `Test failed on provide metric id which is not exists`() {
        runBlocking {
            val testTitle = "Test title"
            val response = createTest(testClient, TestCreateDto(
                title = testTitle,
                type = 1,
                formula = "$1 + $2",
                metric = 0,
                questions = mutableListOf()
            ))

            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `Test failed on bad data`() {
        runBlocking {
            val response = testClient.post(globalEndpoint)
            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }
    }

    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val response = client.post(globalEndpoint)
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }
}