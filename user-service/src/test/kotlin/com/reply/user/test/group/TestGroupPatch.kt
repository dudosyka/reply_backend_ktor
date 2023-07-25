package com.reply.user.test.group

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.group.GroupCreateClientDto
import com.reply.libs.dto.client.group.GroupOutputClientDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime
import kotlin.test.assertEquals

class TestGroupPatch: AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/group"

    private val group = runBlocking {
        val response = TestGroupCreation.createGroup(testClient, "New group", listOf())
        response.body<GroupOutputClientDto>()
    }

    @Test
    fun `Test success patch group`() {
        runBlocking {
            val newGroupName = "${LocalDateTime.now()}"
            val adminData = getTokenData(testClient)
            val response = testClient.patch("$globalEndpoint/${group.id}") {
                setBody<GroupCreateClientDto>(
                    GroupCreateClientDto(
                        name = newGroupName,
                        users = listOf(adminData.id)
                    )
                )
            }

            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<GroupOutputClientDto>()
            }
            assertEquals(newGroupName, body.name)
            assertEquals(listOf(adminData.id), body.users.map { it.id })
        }
    }

    @Test
    fun `Test failed on bad request`() {
        runBlocking {
            val response = testClient.patch("$globalEndpoint/${group.id}")
            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }
    }


    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val response = client.patch("$globalEndpoint/${group.id}")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed if try to patch group from different companies`() {
        runBlocking {
            val adminClient = newAdminRoleClient()
            val response = adminClient.patch("$globalEndpoint/${group.id}")
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun `Test failed on trying to add user from different company to the group `() {
        runBlocking {
            val user = getTokenData(newAdminRoleClient())
            val response = testClient.patch("$globalEndpoint/${group.id}") {
                setBody<GroupCreateClientDto>(
                    GroupCreateClientDto(
                        name = "newGroupName",
                        users = listOf(user.id)
                    )
                )
            }
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun `Test failed on trying to patch group which is not found`() {
        runBlocking {
            val response = testClient.patch("$globalEndpoint/0")
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }
}