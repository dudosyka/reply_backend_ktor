package com.reply.file.test

import com.reply.file.config.NetworkConfig
import com.reply.libs.dto.client.file.FileCreateDto
import com.reply.libs.dto.client.file.FileOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import kotlin.test.Test
import kotlin.test.assertEquals

class TestFileStream: AuthorizedTest() {
    private val adminClient = runBlocking {
        newAdminRoleClient()
    }

    private val fileData = FileCreateDto(
        fileName = "Test file name",
        base64Encoded = "test"
    )

    companion object {
        private val globalEndpoint = "http://localhost:${NetworkConfig.port}/file"
    }

    @Test
    fun `Test success file stream`() {
        runBlocking {
            //Firstly we need to create a file
            val upload = adminClient.post("http://localhost:${NetworkConfig.port}/upload") {
                headers {
                    append("Internal-Request", "true")
                }
                setBody<FileCreateDto>(fileData)
            }
            val fileId = upload.body<FileOutputDto>().id

            val stream = adminClient.get("$globalEndpoint/$fileId")
            assertEquals(HttpStatusCode.OK, stream.status)
        }
    }

    @Test
    fun `Test failed on file not found`() {
        runBlocking {
            val response = adminClient.get("$globalEndpoint/0")
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }

    @Test
    fun `Test failed on not authorized`() {
        runBlocking {
            val response = client.get("$globalEndpoint/0")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }
}