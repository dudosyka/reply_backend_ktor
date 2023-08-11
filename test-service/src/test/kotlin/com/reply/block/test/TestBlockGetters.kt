package com.reply.block.test.block

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.block.BlockCreateDto
import com.reply.libs.dto.client.block.BlockOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.Test

class TestBlockGetters: AuthorizedTest() {

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
        delay(1000)
        val response = client.post(globalEndpoint) {
            setBody<BlockCreateDto>(
                data
            )
        }
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
            assertEquals(0, body.size)
        }
    }
    @Test
    fun `Test success get one block`() {
        runBlocking {
            val block = createBlock(testClient)
            val response = testClient.get("$globalEndpoint/${block.id}")
            assertEquals(HttpStatusCode.OK, response.status)

            val body = assertDoesNotThrow {
                response.body<BlockOutputDto>()
            }
            assertEquals(createdBlockName, body.name)
        }
    }
    @Test
    fun `Test success get blocks by companyId `() {
        runBlocking {
            val newClient = newAdminRoleClient()
            val newClientData = getTokenData(newClient)
            delay(1000)
            createBlock(newClient)
            delay(1000)
            val response = testClient.get("$globalEndpoint/company/${newClientData.companyId}")
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
            val response = client.get("$globalEndpoint/0")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed on trying to access block which is from different company`() {
        runBlocking {
            //Firstly create block from test client
            val block = createBlock(testClient)

            //Then trying to remove block from second admin client
            val secondAdmin = newAdminRoleClient()
            val response = secondAdmin.get("$globalEndpoint/${block.id}")
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }
}