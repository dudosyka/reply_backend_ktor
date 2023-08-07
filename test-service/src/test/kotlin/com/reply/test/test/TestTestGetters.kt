package com.reply.test.test

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.group.GroupOutputClientDto
import com.reply.libs.dto.client.question.QuestionCreateDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class TestTestGetters : AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/test"

    val createdTestName = "New test"
    val createdTestQuestions = mutableListOf<QuestionCreateDto>()
    private val test = runBlocking {
        val response = TestTestCreation.createTest(testClient, createdTestName, createdTestQuestions)
        response.body<GroupOutputClientDto>()
    }


    @Test
    fun `Test success get tests`() {
        runBlocking {
            val response = testClient.get(globalEndpoint)
            assertEquals(HttpStatusCode.OK, response.status)

            val body = assertDoesNotThrow {
                response.body<List<TestOutputDto>>()
            }
            assertEquals(1, body.size)
            assertEquals(createdTestName, body.first().title)
        }
    }
    @Test
    fun `Test success get one test`() {
        runBlocking {
            val response = testClient.get("$globalEndpoint/${test.id}")
            assertEquals(HttpStatusCode.OK, response.status)

            val body = assertDoesNotThrow {
                response.body<GroupOutputClientDto>()
            }
            assertEquals(createdTestName, body.name)
        }
    }
}