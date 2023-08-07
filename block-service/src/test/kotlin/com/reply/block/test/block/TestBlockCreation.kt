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

class TestBlockCreation: AuthorizedTest() {

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
        data.tests = listOf(test.id)
        client.post(globalEndpoint) {
            setBody<BlockCreateDto>(
                data
            )
        }
    }

    @Test
    fun `Test success block creation`() {
        runBlocking {
            val response = createBlock(testClient)

            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<BlockOutputDto>()
            }
            assertEquals(createdBlockName, body.name)
            assertEquals(1, body.tests.size)
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

    @Test
    fun `Test failed on providing tests which are not exist`() {
        runBlocking {
            val response = testClient.post(globalEndpoint) {
                setBody<BlockCreateDto>(
                    BlockCreateDto(
                        name = createdBlockName,
                        description = "",
                        time = 1,
                        tests = listOf(0)
                    )
                )
            }

            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }
}