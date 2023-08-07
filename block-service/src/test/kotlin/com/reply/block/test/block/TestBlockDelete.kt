package com.reply.block.test.block

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.block.BlockOutputDto
import com.reply.libs.dto.client.group.GroupOutputClientDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class TestBlockDelete: AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/block"

    @Test
    fun `Test success block deleting`() {
        runBlocking {
            val block = suspend {
                val response = TestBlockCreation.createBlock(testClient, "New block", listOf())
                response.body<BlockOutputDto>()
            }()
            val response = testClient.delete("$globalEndpoint/${block.id}")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<SuccessOutputDto>()
            }
            assertEquals("success", body.status)
            assertEquals("Block successfully deleted", body.msg)
        }
    }

    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val block = suspend {
                val response = TestBlockCreation.createBlock(testClient, "New block", listOf())
                response.body<BlockOutputDto>()
            }()
            val response = client.delete("$globalEndpoint/${block.id}")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed on trying to delete block from different company`() {
        runBlocking {
            val adminClient = newAdminRoleClient()
            val block = suspend {
                val response = TestBlockCreation.createBlock(testClient, "New block", listOf())
                response.body<GroupOutputClientDto>()
            }()
            val response = adminClient.delete("$globalEndpoint/${block.id}")
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun `Test failed on trying to delete block which is not found`() {
        runBlocking {
            val response = testClient.delete("$globalEndpoint/0")
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }
}