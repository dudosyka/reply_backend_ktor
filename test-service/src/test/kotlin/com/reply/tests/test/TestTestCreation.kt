package com.reply.tests.test

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.question.QuestionCreateDto
import com.reply.libs.dto.client.test.TestCreateDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class TestTestCreation: AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    companion object {
        private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/test"
        fun createTest(client: HttpClient, title: String, questions: MutableList<QuestionCreateDto>) =
            runBlocking {
                client.post(globalEndpoint) {
                    setBody<TestCreateDto>(
                        TestCreateDto(
                            title = title,
                            type = 2,
                            formula = "$1 + $2",
                            metric = 1,
                            questions = questions
                    )
                    )
                }
            }
    }

    @Test
    fun `Test success test creation`() {
        runBlocking {
            val testTitle = "Test title"
            val response = createTest(testClient, testTitle, mutableListOf())

            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<TestOutputDto>()
            }
            assertEquals(testTitle, body.title)
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