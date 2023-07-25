package com.reply.user.test.group

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.group.GroupOutputClientDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import kotlin.test.assertEquals

class TestGroupDelete: AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/group"

    @Test
    fun `Test success group deleting`() {
        runBlocking {
            val group = suspend {
                val response = TestGroupCreation.createGroup(testClient, "New group", listOf())
                response.body<GroupOutputClientDto>()
            }()
            val response = testClient.delete("$globalEndpoint/${group.id}")

            assertEquals(HttpStatusCode.OK, response.status)
            val body = assertDoesNotThrow {
                response.body<SuccessOutputDto>()
            }
            assertEquals("success", body.status)
            assertEquals("Group successfully deleted", body.msg)
        }
    }

    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val group = suspend {
                val response = TestGroupCreation.createGroup(testClient, "New group", listOf())
                response.body<GroupOutputClientDto>()
            }()
            val response = client.delete("$globalEndpoint/${group.id}")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed on trying to delete group from different company`() {
        runBlocking {
            val adminClient = newAdminRoleClient()
            val group = suspend {
                val response = TestGroupCreation.createGroup(testClient, "New group", listOf())
                response.body<GroupOutputClientDto>()
            }()
            val response = adminClient.delete("$globalEndpoint/${group.id}")
            assertEquals(HttpStatusCode.Forbidden, response.status)
        }
    }

    @Test
    fun `Test failed on trying to delete group which is not found`() {
        runBlocking {
            val response = testClient.delete("$globalEndpoint/0")
            assertEquals(HttpStatusCode.NotFound, response.status)
        }
    }
}