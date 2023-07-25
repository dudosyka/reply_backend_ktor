package com.reply.user.test.group

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.group.GroupCreateClientDto
import com.reply.libs.dto.client.group.GroupOutputClientDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class TestGroupCreation: AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    companion object {
        private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/group"
        fun createGroup(client: HttpClient, groupName: String, users: List<Int>) =
            runBlocking {
                client.post(globalEndpoint) {
                    setBody<GroupCreateClientDto>(GroupCreateClientDto(
                        name = groupName,
                        users = users
                    ))
                }
            }
    }

    @Test
    fun `Test success group creation`() {
        runBlocking {
            val adminData = getTokenData(testClient)
            val groupName = "Test group"
            val response = createGroup(testClient, groupName, listOf(adminData.id))

            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<GroupOutputClientDto>()
            }
            assertEquals(groupName, body.name)
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