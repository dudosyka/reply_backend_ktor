package com.reply.block.test.block

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.block.BlockCreateDto
import com.reply.libs.dto.client.block.BlockOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime
import kotlin.test.assertEquals

class TestBlockDelete: AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/block"

    private val createdBlockName = "BlockName${LocalDateTime.now()}"

    private fun createBlock(client: HttpClient, data: BlockCreateDto = BlockCreateDto(
        name = createdBlockName,
        description = "",
        time = 1,
        tests = listOf()
    )) = runBlocking {
        val response = client.post(globalEndpoint) {
            setBody<BlockCreateDto>(
                data
            )
        }
        response.body<BlockOutputDto>()
    }

    @Test
    fun `Test success block deleting`() {
        runBlocking {
            val block = createBlock(testClient)

            val response = testClient.delete("$globalEndpoint/${block.id}")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<SuccessOutputDto>()
            }
            assertEquals("success", body.status)
            assertEquals("Block successfully removed", body.msg)
        }
    }

    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val block = createBlock(testClient)
            val response = client.delete("$globalEndpoint/${block.id}")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed on trying to delete block from different company`() {
        runBlocking {
            //Firstly create block with test client
            val block = createBlock(testClient)

            //Then trying to remove with second admin client
            val secondAdmin = newAdminRoleClient()
            val response = secondAdmin.delete("$globalEndpoint/${block.id}")
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