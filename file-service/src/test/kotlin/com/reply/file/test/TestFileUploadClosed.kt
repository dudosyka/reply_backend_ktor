package com.reply.file.test

import com.reply.file.config.NetworkConfig
import com.reply.libs.dto.client.file.FileCreateDto
import com.reply.libs.dto.client.file.FileOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.Test
import kotlin.test.assertEquals

class TestFileUploadClosed: AuthorizedTest() {

    private val adminClient = runBlocking {
        newAdminRoleClient()
    }

    private val fileData = FileCreateDto(
        fileName = "Test file name",
        base64Encoded = "test"
    )

    companion object {
        private val globalEndpoint = "http://localhost:${NetworkConfig.port}/closed/upload"
    }

    @Test
    fun `Test success file uploading`() {
        runBlocking {
            val response = adminClient.post(globalEndpoint) {
                headers {
                    append("Internal-Request", "true")
                }
                setBody<FileCreateDto>(fileData)
            }

            assertEquals(HttpStatusCode.OK, response.status)
            assertDoesNotThrow {
                response.body<FileOutputDto>()
            }
        }
    }

    @Test
    fun `Test failed on bad request`() {
        runBlocking {
            val response = adminClient.post(globalEndpoint) {
                headers {
                    append("Internal-Request", "true")
                }
            }
            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }
    }

    @Test
    fun `Test failed on not authorized`() {
        runBlocking {
            val response = client.post(globalEndpoint) {
                headers {
                    append("Internal-Request", "true")
                }
                setBody(fileData)
            }

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }
}