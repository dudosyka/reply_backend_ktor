package com.reply.test.test

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.test.TestCreateDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class TestTestDelete : AuthorizedTest() {

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
    )) = run {
        val create = client.post(globalEndpoint) {
            setBody<TestCreateDto>(
                data
            )
        }
        create.body<TestOutputDto>()
    }

    @Test
    fun `Test success test deleting`() {
        runBlocking {
            val test = createNewTest(testClient)

            val response = testClient.delete("$globalEndpoint/${test.id}")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<SuccessOutputDto>()
            }
            assertEquals("success", body.status)
            assertEquals("Test successfully removed", body.msg)
        }
    }

    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val test = createNewTest(testClient)

            val response = client.delete("$globalEndpoint/${test.id}")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed on try to delete built-in test`() {
        runBlocking {
            val response = testClient.delete("$globalEndpoint/1")
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun `Test failed on try delete test from different company`() {
        runBlocking {
            //Firstly create test with testClient
            val test = createNewTest(testClient)

            //Then try to delete created test with second admin
            val response = secondAdmin.delete("$globalEndpoint/${test.id}")

            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun `Test failed on trying to delete test which is not found`() {
        runBlocking {
            val response = testClient.delete("$globalEndpoint/0")
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }
}