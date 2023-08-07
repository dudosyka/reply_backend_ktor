package com.reply.file.test

import com.reply.file.config.NetworkConfig
import com.reply.libs.dto.client.base.SuccessOutputDto
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

class TestFileRollback: AuthorizedTest() {
    private val adminClient = runBlocking {
        newAdminRoleClient()
    }

    private val fileData = FileCreateDto(
        fileName = "Test file name",
        base64Encoded = "test"
    )

    companion object {
        private val globalEndpoint = "http://localhost:${NetworkConfig.port}/rollback"
    }

    @Test
    fun `Test success file rollback`() {
        runBlocking {
            //Firstly we need to create a file
            val upload = adminClient.post("http://localhost:${NetworkConfig.port}/upload") {
                headers {
                    append("Internal-Request", "true")
                }
                setBody<FileCreateDto>(fileData)
            }
            val fileId = upload.body<FileOutputDto>().id

            val response = adminClient.delete("$globalEndpoint/$fileId") {
                headers {
                    append("Internal-Request", "true")
                }
            }

            assertEquals(HttpStatusCode.OK, response.status)
            assertDoesNotThrow {
                response.body<SuccessOutputDto>()
            }
        }
    }

    @Test
    fun `Test failed on file not found`() {
        runBlocking {
            val response = adminClient.delete("$globalEndpoint/0") {
                headers {
                    append("Internal-Request", "true")
                }
            }

            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `Test failed on not internal request`() {
        runBlocking {
            val response = adminClient.delete("$globalEndpoint/0")

            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }
}