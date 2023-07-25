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

class TestGroupUsersManaging: AuthorizedTest() {

    private val testClient = runBlocking {
        newAdminRoleClient()
    }

    private val testClientData = runBlocking {
        getTokenData(testClient)
    }

    private val secondTestClient = runBlocking {
        getTokenData(newClientRoleClient(companyId = testClientData.companyId))
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/group"

    private val group = runBlocking {
        val response = TestGroupCreation.createGroup(testClient, "New group", listOf(secondTestClient.id))
        response.body<GroupOutputClientDto>()
    }

    @Test
    fun `Test success append user to group`() {
        runBlocking {
            val response = testClient.patch("$globalEndpoint/${group.id}/user/${testClientData.id}/append")
            assertEquals(HttpStatusCode.OK, response.status)

            val body = assertDoesNotThrow {
                response.body<SuccessOutputDto>()
            }
            assertEquals("User successfully added to group", body.msg)
        }
    }

    @Test
    fun `Test success remove user from group`() {
        runBlocking {
            val response = testClient.patch("$globalEndpoint/${group.id}/user/${secondTestClient.id}/remove")
            assertEquals(HttpStatusCode.OK, response.status)

            val body = assertDoesNotThrow {
                response.body<SuccessOutputDto>()
            }
            assertEquals("User successfully removed from group", body.msg)
        }
    }

    @Test
    fun `Test failed on unauthorized`() {
        runBlocking {
            val response = client.patch("$globalEndpoint/${group.id}/user/${secondTestClient.id}/remove")
            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }

    @Test
    fun `Test failed on trying to add user from different company`() {
        runBlocking {
            val response = testClient.patch("$globalEndpoint/${group.id}/user/${secondTestClient.id}/append")
            assertEquals(HttpStatusCode.BadRequest, response.status)
        }
    }


}