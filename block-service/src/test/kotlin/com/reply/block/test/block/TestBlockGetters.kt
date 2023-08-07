package com.reply.block.test.block

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.block.BlockOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class TestBlockGetters: AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/block"
    private val createdBlockName = "TestBlockName"
    private val createdBlockTests = listOf<Int>(1)

    private val block = runBlocking {
        val response = TestBlockCreation.createBlock(testClient, createdBlockName, createdBlockTests)
        response.body<BlockOutputDto>()
    }

    @Test
    fun `Test success get blocks`() {
        runBlocking {
            val response = testClient.get(globalEndpoint)
            assertEquals(HttpStatusCode.OK, response.status)

            val body = assertDoesNotThrow {
                response.body<List<BlockOutputDto>>()
            }
            assertEquals(1, body.size)
            assertEquals(createdBlockName, body.first().name)
        }
    }
    @Test
    fun `Test success get one block`() {
        runBlocking {
            val response = testClient.get("$globalEndpoint/${block.id}")
            assertEquals(HttpStatusCode.OK, response.status)

            val body = assertDoesNotThrow {
                response.body<BlockOutputDto>()
            }
            assertEquals(createdBlockName, body.name)
            assertEquals(createdBlockTests, body.tests.map { it.id })
        }
    }
    @Test
    fun `Test success get blocks by companyId `() {
        runBlocking {
            val response = testClient.get("$globalEndpoint/company/8")
            assertEquals(HttpStatusCode.OK, response.status)

            val body = assertDoesNotThrow {
                response.body<List<BlockOutputDto>>()
            }
            assertEquals(1, body.size)
            assertEquals(createdBlockName, body.first().name)
        }
    }

    @Test
    fun `Test failed on unauthorized get all blocks`() {
        runBlocking {
            val response = client.get(globalEndpoint)
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed on unauthorized get one block`() {
        runBlocking {
            val response = client.get("$globalEndpoint/${block.id}")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed on trying to access block which is from different company`() {
        runBlocking {
            val adminClient = newAdminRoleClient()
            val response = adminClient.get("$globalEndpoint/${block.id}")
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }
}