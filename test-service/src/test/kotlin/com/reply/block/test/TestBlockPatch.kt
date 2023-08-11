package com.reply.block.test.block

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.block.BlockCreateDto
import com.reply.libs.dto.client.block.BlockOutputDto
import com.reply.libs.dto.client.test.TestCreateDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime
import kotlin.test.assertEquals
import kotlin.test.Test

class TestBlockPatch : AuthorizedTest() {

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
        val createTest = testClient.post("http://localhost:80${ApiConfig.adminEndpoint}/test") {
            setBody<TestCreateDto>(
                TestCreateDto(
                    title = "title",
                    type = 2,
                    formula = "$1 + $2",
                    metric = 1,
                    questions = mutableListOf()
                )
            )
        }
        val test = createTest.body<TestOutputDto>()
        val response = client.post(globalEndpoint) {
            setBody<BlockCreateDto>(
                data
            )
        }
        Pair(response.body<BlockOutputDto>(), test)
    }

    @Test
    fun `Test success patch block`() {
        runBlocking {
            val newBlockName = "${LocalDateTime.now()}"
            val block = createBlock(testClient).first
            val response = testClient.patch("$globalEndpoint/${block.id}") {
                setBody<BlockCreateDto>(
                    BlockCreateDto(
                        name = newBlockName,
                        description = "description",
                        time = 1,
                        tests = listOf()
                    )
                )
            }

            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<BlockOutputDto>()
            }
            assertEquals(newBlockName, body.name)
            assertEquals(0, body.tests.size)
        }
    }

    @Test
    fun `Test failed on bad request`() {
        runBlocking {
            val block = createBlock(testClient).first
            val response = testClient.patch("$globalEndpoint/${block.id}")
            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }
    }


    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val response = client.patch("$globalEndpoint/0")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed if try to patch block from different companies`() {
        runBlocking {
            //Firstly create block from test client
            val block = createBlock(testClient).first

            //Then trying to patch it from second admin client
            val secondAdmin = newAdminRoleClient()
            val response = secondAdmin.patch("$globalEndpoint/${block.id}") {
                setBody<BlockCreateDto>(
                    BlockCreateDto(
                        name = "newGroupName",
                        description = "",
                        time = 2,
                        tests = listOf()
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
                        name = "newGroupName",
                        description = "",
                        time = 2,
                        tests = listOf()
                    )
                )
            }
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `Test failed on trying to patch block with tests which is from different company`() {
        runBlocking {
            val created = createBlock(testClient)
            val test = created.second

            val secondAdmin = newAdminRoleClient()
            val secondBlock = createBlock(secondAdmin)

            val response = secondAdmin.patch("$globalEndpoint/${secondBlock.first.id}") {
                setBody<BlockCreateDto>(
                    BlockCreateDto(
                        name = "newGroupName",
                        description = "",
                        time = 2,
                        tests = listOf(test.id)
                    )
                )
            }

            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }
}