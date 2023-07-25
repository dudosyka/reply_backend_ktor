package com.reply.user.test.company

import com.reply.libs.config.ApiConfig
import com.reply.libs.dto.client.company.CompanyCreateDto
import com.reply.libs.dto.client.company.CompanyOutputDto
import com.reply.libs.dto.client.company.CompanyUserDto
import com.reply.libs.dto.client.file.FileCreateDto
import com.reply.libs.dto.client.group.GroupOutputDto
import com.reply.libs.test.AuthorizedTest
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime
import kotlin.test.assertEquals

class TestCompanyActions: AuthorizedTest() {
    private val companyName = "company-${LocalDateTime.now()}"
    //On start-up we create new test client
    private var testClient: HttpClient = runBlocking {
        newAdminRoleClient(companyName = companyName)
    }

    private val globalEndpoint = "http://localhost:80${ApiConfig.adminEndpoint}/company"

    @Test
    fun `Test getter returns actual company`() {
        runBlocking {
            val response = testClient.get(globalEndpoint)

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = assertDoesNotThrow {
                response.body<CompanyOutputDto>()
            }
            assertEquals(companyName, responseBody.name)
        }
    }

    //We will check if there are any company for user which we create on init
    @Test
    fun `Test success new company creation`() {
        runBlocking {
            val response = testClient.get(globalEndpoint)

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = assertDoesNotThrow {
                response.body<CompanyOutputDto>()
            }

            //Check that is company which we created earlier on init
            assertEquals(companyName, responseBody.name)
        }
    }

    @Test
    fun `Test patch successfully`() {
        runBlocking {
            // Firstly check that we can edit company data,
            // so it must return success-edited response
            val newName = "new-company-name"
            val response = testClient.patch(globalEndpoint) {
                setBody<CompanyCreateDto>(
                    CompanyCreateDto(
                        name = newName,
                        FileCreateDto(
                            "test",
                            "test"
                        )
                    )
                )
            }

            assertEquals(HttpStatusCode.OK, response.status)

            val responseBody = assertDoesNotThrow {
                response.body<CompanyOutputDto>()
            }

            assertEquals(newName, responseBody.name)


            // Secondly lets check that data was actually saved
            // We will get company data from API by current client
            // Data must be equals to data we saved
            val responseGetter = testClient.get(globalEndpoint)

            assertEquals(HttpStatusCode.OK, response.status)

            val responseGetterBody = assertDoesNotThrow {
                responseGetter.body<CompanyOutputDto>()
            }

            assertEquals(newName, responseGetterBody.name)
        }
    }

    @Test
    fun `Test bad data patch`() {
        runBlocking {
            val response = testClient.patch(globalEndpoint)

            assertEquals(HttpStatusCode.UnsupportedMediaType, response.status)
        }
    }

    private suspend fun getCompanyUsers(client: HttpClient): List<CompanyUserDto> {
        val response = client.get("$globalEndpoint/users")

        assertEquals(HttpStatusCode.OK, response.status)
        return assertDoesNotThrow {
            response.body<List<CompanyUserDto>>()
        }
    }

    @Test
    fun `Test success users getter`() {
        runBlocking {
            val newAdmin = newAdminRoleClient()
            val companyUsers = getCompanyUsers(newAdmin)
            //In new company we wait that only admin will be in
            assertEquals(1, companyUsers.size)

            val adminData = getTokenData(newAdmin)
            val newClientLogin = "new-client-${LocalDateTime.now()}"

            //Let`s create one client
            newClientRoleClient(login = newClientLogin,companyId = adminData.companyId)

            val companyUsersAfterCreation = getCompanyUsers(newAdmin)
            assertEquals(2, companyUsersAfterCreation.size)
        }
    }

    @Test
    fun `Test success groups getter`() {
        runBlocking {
            val response = testClient.get("$globalEndpoint/groups")

            assertEquals(HttpStatusCode.OK, response.status)
            val companyGroups = assertDoesNotThrow {
                response.body<List<GroupOutputDto>>()
            }

            //We wait for zero groups in newly company
            assertEquals(0, companyGroups.size)
        }
    }

    @Test
    fun `Test unauthorized when call resource without token`() {
        runBlocking {
            val response = client.patch(globalEndpoint)

            assertEquals(HttpStatusCode.Unauthorized, response.status)
        }
    }
}