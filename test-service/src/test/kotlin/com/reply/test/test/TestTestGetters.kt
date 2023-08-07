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
import kotlin.test.Test
import kotlin.test.assertEquals

class TestTestGetters : AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val secondAdmin = runBlocking {
        newAdminRoleClient()
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/test"

    private val createdTestName = "New test"
    private suspend fun createNewTest(client: HttpClient, data: TestCreateDto = TestCreateDto(
        title = createdTestName,
        type = 2,
        formula = "$1 + $2",
        metric = 1,
        questions = mutableListOf()
    )) = run {
        val create = client.post(globalEndpoint) {
            setBody<TestCreateDto>(
                data
            )
        }
        create.body<TestOutputDto>()
    }

    @Test
    fun `Test success get tests`() {
        runBlocking {
            val response = testClient.get(globalEndpoint)
            assertEquals(HttpStatusCode.OK, response.status)

            val body = assertDoesNotThrow {
                response.body<List<TestOutputDto>>()
            }
            //It should return one built-in test because user is empty
            assertEquals(1, body.size)
        }
    }
    @Test
    fun `Test success get one test`() {
        runBlocking {
            val test = createNewTest(testClient)
            val response = testClient.get("$globalEndpoint/${test.id}")
            assertEquals(HttpStatusCode.OK, response.status)

            val body = assertDoesNotThrow {
                response.body<TestOutputDto>()
            }
            assertEquals(createdTestName, body.title)
        }
    }

    @Test
    fun `Test failed on trying to access test from different company`() {
        runBlocking {
            //Firstly create test from testClient user
            val test = createNewTest(testClient)

            //Then trying to access created test from second admin user
            val response = secondAdmin.get("$globalEndpoint/${test.id}")
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val response = client.get("$globalEndpoint/0")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }
}