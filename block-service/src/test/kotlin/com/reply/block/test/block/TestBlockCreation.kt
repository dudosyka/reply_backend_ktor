package com.reply.block.test.block

import com.reply.libs.config.ApiConfig
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
import kotlin.test.assertEquals

class TestBlockCreation: AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    companion object {
        private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/block"
        fun createBlock(client: HttpClient, blockName: String, tests: List<Int>) =
            runBlocking {
                client.post(globalEndpoint) {
                    setBody<BlockCreateDto>(
                        BlockCreateDto(
                        name = blockName,
                        description = "",
                        time = 1,
                        tests = tests
                    )
                    )
                }
            }
    }
    @Test
    fun `Test success block creation`() {
        runBlocking {
            val adminData = getTokenData(testClient)
            val blockName = "Test block"
            val response = createBlock(testClient, blockName, listOf(adminData.id))

            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<BlockOutputDto>()
            }
            assertEquals(blockName, body.name)
        }
    }

    @Test
    fun `Test failed on bad data`() {
        runBlocking {
            val response = testClient.post(globalEndpoint)
            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }
    }

    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val response = client.post(globalEndpoint)
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }
}