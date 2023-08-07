package com.reply.user.test.group

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.group.GroupOutputClientDto
import com.reply.libs.dto.client.group.GroupOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class TestGroupGetters: AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/group"

    private val createdGroupName = "New group"
    private val createdGroupUsers = runBlocking {
        listOf<Int>(getTokenData(testClient).id)
    }
    private val group = runBlocking {
        val response = TestGroupCreation.createGroup(testClient, createdGroupName, createdGroupUsers)
        response.body<GroupOutputClientDto>()
    }


    @Test
    fun `Test success get groups`() {
        runBlocking {
            val response = testClient.get(globalEndpoint)
            assertEquals(HttpStatusCode.OK, response.status)

            val body = assertDoesNotThrow {
                response.body<List<GroupOutputDto>>()
            }
            assertEquals(1, body.size)
            assertEquals(createdGroupName, body.first().name)
        }
    }

    @Test
    fun `Test success get one group`() {
        runBlocking {
            val response = testClient.get("$globalEndpoint/${group.id}")
            assertEquals(HttpStatusCode.OK, response.status)

            val body = assertDoesNotThrow {
                response.body<GroupOutputClientDto>()
            }
            assertEquals(createdGroupName, body.name)
            assertEquals(createdGroupUsers, body.users.map { it.id })
        }
    }

    @Test
    fun `Test failed on unauthorized get all groups`() {
        runBlocking {
            val response = client.get(globalEndpoint)
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed on unauthorized get one group`() {
        runBlocking {
            val response = client.get("$globalEndpoint/${group.id}")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed on trying to access group which is from different company`() {
        runBlocking {
            val adminClient = newAdminRoleClient()
            val response = adminClient.get("$globalEndpoint/${group.id}")
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }
}