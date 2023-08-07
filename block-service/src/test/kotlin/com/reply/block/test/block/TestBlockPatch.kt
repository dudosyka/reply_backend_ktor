package com.reply.block.test.block

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.block.BlockCreateDto
import com.reply.libs.dto.client.block.BlockOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime
import kotlin.test.assertEquals

class TestBlockPatch : AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/block"

    private val block = runBlocking {
        val response = TestBlockCreation.createBlock(testClient, "New block", listOf())
        response.body<BlockOutputDto>()
    }

    @Test
    fun `Test success patch block`() {
        runBlocking {
            val newBlockName = "${LocalDateTime.now()}"
            val response = testClient.patch("$globalEndpoint/${block.id}") {
                setBody<BlockCreateDto>(
                    BlockCreateDto(
                        name = newBlockName,
                        description = "description",
                        time = 1,
                        tests = listOf(1)
                    )
                )
            }

            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<BlockOutputDto>()
            }
            assertEquals(newBlockName, body.name)
            assertEquals(listOf(1), body.tests.map { it.id })
        }
    }

    @Test
    fun `Test failed on bad request`() {
        runBlocking {
            val response = testClient.patch("$globalEndpoint/${block.id}")
            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }
    }


    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val response = client.patch("$globalEndpoint/${block.id}")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed if try to patch block from different companies`() {
        runBlocking {
            val adminClient = newAdminRoleClient()
            val response = adminClient.patch("$globalEndpoint/${block.id}") {
                setBody<BlockCreateDto>(
                    BlockCreateDto(
                        //Fake data
                        name = "newGroupName",
                        description = "",
                        time = 2,
                        tests = listOf(123321)
                    )
                )
            }
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun `Test failed on trying to patch block which is not found`() {
        runBlocking {
            val response = testClient.patch("$globalEndpoint/0") {
                setBody<BlockCreateDto>(
                    BlockCreateDto(
                        //Fake data
                        name = "newGroupName",
                        description = "",
                        time = 2,
                        tests = listOf(123321)
                    )
                )
            }
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }
}