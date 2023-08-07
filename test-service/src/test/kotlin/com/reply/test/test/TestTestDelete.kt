package com.reply.test.test

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class TestTestDelete : AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/test"

    @Test
    fun `Test success test deleting`() {
        runBlocking {
            val test = suspend {
                val response = TestTestCreation.createTest(testClient, "New test", mutableListOf())
                response.body<TestOutputDto>()
            }()
            val response = testClient.delete("$globalEndpoint/${test.id}")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<SuccessOutputDto>()
            }
            assertEquals("success", body.status)
            assertEquals("Test successfully deleted", body.msg)
        }
    }

    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val test = suspend {
                val response = TestTestCreation.createTest(testClient, "New test", mutableListOf())
                response.body<TestOutputDto>()
            }()
            val response = client.delete("$globalEndpoint/${test.id}")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
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