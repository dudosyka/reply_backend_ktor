package com.reply.block.test

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.auth.AuthOutputDto
import com.reply.libs.dto.client.block.BlockCreateDto
import com.reply.libs.dto.client.block.BlockOnPassDto
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

class TestBlockProvideDataByAccessToken: AuthorizedTest() {
    private val testClient = runBlocking {
        newAdminRoleClient()
    }
    private val testClientData = runBlocking {
        getTokenData(testClient)
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.authorizedEndpoint}/block/pass"

    private val blockName = "BlockName${LocalDateTime.now()}"

    private val testClientBlockToken = runBlocking {
        val response = testClient.post("http://localhost:80${ApiConfig.adminEndpoint}/block") {
            setBody<BlockCreateDto>(
                BlockCreateDto(
                    name = blockName,
                    description = "",
                    time = 1,
                    tests = listOf()
                )
            )
        }

        val block = response.body<BlockOutputDto>()

        val tokenResponse = testClient.post("http://localhost:80${ApiConfig.adminEndpoint}/block/token") {
            setBody<BlockTokenDto>(
                BlockTokenDto(
                    blockId = block.id,
                    week = 1,
                    userId = testClientData.id
                )
            )
        }

        tokenResponse.body<AuthOutputDto>().token
    }

    private val testClientPass = createAuthorizedClient(testClientBlockToken)

    @Test
    fun `Test success resolve block data by token`() {
        runBlocking {
            val response = testClientPass.get(globalEndpoint)

            assertEquals(HttpStatusCode.OK, response.status)
            val data = assertDoesNotThrow {
                response.body<BlockOnPassDto>()
            }
            assertEquals(blockName, data.name)
        }
    }

    @Test
    fun `Test failed on bad token`() {
        runBlocking {
            val response = testClient.get(globalEndpoint)

            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }
}