package com.reply.block.test

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.auth.AuthOutputDto
import com.reply.libs.dto.client.block.BlockCreateDto
import com.reply.libs.dto.client.block.BlockOutputDto
import com.reply.libs.dto.client.block.BlockTokenDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class TestBlockAccessTokenGenerator: AuthorizedTest() {
    private val testClient = runBlocking {
        newAdminRoleClient()
    }
    private val testClientData = runBlocking {
        getTokenData(testClient)
    }

    private val secondAdmin = runBlocking {
        newAdminRoleClient()
    }
    private val secondAdminData = runBlocking {
        getTokenData(secondAdmin)
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/block/token"

    private val testClientBlock = runBlocking {
        val response = testClient.post("http://localhost:80${ApiConfig.adminEndpoint}/block") {
            setBody<BlockCreateDto>(
                BlockCreateDto(
                    name = "BlockName${LocalDateTime.now()}",
                    description = "",
                    time = 1,
                    tests = listOf()
                )
            )
        }

        response.body<BlockOutputDto>()
    }

    private val secondAdminBlock = runBlocking {
        val response = secondAdmin.post("http://localhost:80${ApiConfig.adminEndpoint}/block") {
            setBody<BlockCreateDto>(
                BlockCreateDto(
                    name = "BlockName${LocalDateTime.now()}",
                    description = "",
                    time = 1,
                    tests = listOf()
                )
            )
        }

        response.body<BlockOutputDto>()
    }

    @Test
    fun `Test success resolve block pass token`() {
        runBlocking {
            val response = testClient.post(globalEndpoint) {
                setBody<BlockTokenDto>(
                    BlockTokenDto(
                        blockId = testClientBlock.id,
                        week = 1,
                        userId = testClientData.id
                    )
                )
            }

            assertEquals(HttpStatusCode.OK, response.status)
            assertDoesNotThrow {
                response.body<AuthOutputDto>()
            }
        }
    }

    @Test
    fun `Test failed on user not found`() {
        runBlocking {
            val response = testClient.post(globalEndpoint) {
                setBody<BlockTokenDto>(
                    BlockTokenDto(
                        blockId = testClientBlock.id,
                        week = 1,
                        userId = -1
                    )
                )
            }

            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `Test failed on block not found`() {
        runBlocking {
            val response = testClient.post(globalEndpoint) {
                setBody<BlockTokenDto>(
                    BlockTokenDto(
                        blockId = -1,
                        week = 1,
                        userId = testClientData.id
                    )
                )
            }

            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `Test failed on block from different company`() {
        runBlocking {
            val response = testClient.post(globalEndpoint) {
                setBody<BlockTokenDto>(
                    BlockTokenDto(
                        blockId = secondAdminBlock.id,
                        week = 1,
                        userId = testClientData.id
                    )
                )
            }

            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun `Test failed on user form different company`() {
        runBlocking {
            val response = testClient.post(globalEndpoint) {
                setBody<BlockTokenDto>(
                    BlockTokenDto(
                        blockId = testClientBlock.id,
                        week = 1,
                        userId = secondAdminData.id
                    )
                )
            }

            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }
}