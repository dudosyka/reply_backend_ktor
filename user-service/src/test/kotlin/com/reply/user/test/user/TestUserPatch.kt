package com.reply.user.test.user

import com.reply.libs.dto.client.user.UserOutputDto
import com.reply.libs.dto.client.user.UserUpdateDto
import com.reply.libs.dto.internal.exceptions.DuplicateEntryException
import com.reply.libs.test.AuthorizedTest
import com.reply.libs.utils.bcrypt.PasswordUtil
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.assertDoesNotThrow
import java.time.LocalDateTime
import kotlin.test.Test
import kotlin.test.assertEquals

class TestUserPatch: AuthorizedTest() {
    private val adminUser = runBlocking {
        newAdminRoleClient()
    }
    private val adminUserData = runBlocking {
        getTokenData(adminUser)
    }

    private val secondAdminUser = runBlocking {
        newAdminRoleClient()
    }
    private val secondAdminUserData = runBlocking {
        getTokenData(secondAdminUser)
    }

    private val clientUser = runBlocking {
        newClientRoleClient(companyId = adminUserData.companyId)
    }
    private val clientUserData = runBlocking {
        getTokenData(clientUser)
    }

    private val newLogin = "login${LocalDateTime.now()}"
    private val updateDto = UserUpdateDto(
        login = newLogin,
        email = newLogin,
        phone = newLogin,
        avatar = null,
        fullname = newLogin,
        password = newLogin
    )

    @Test
    fun `Test success patch`() {
        runBlocking {

            val patchResponse = adminUser.patch("${authorizedEndpoint}/user/${clientUserData.id}") {
                setBody(
                    updateDto
                )
            }


            assertEquals(HttpStatusCode.OK, patchResponse.status)
            val patchBody = assertDoesNotThrow {
                patchResponse.body<UserOutputDto>()
            }

            assertEquals(newLogin, patchBody.login)
            assertEquals(newLogin, patchBody.email)
            assertEquals(newLogin, patchBody.phone)
            assertEquals(newLogin, patchBody.fullname)
            assertEquals(null, patchBody.avatar)
            assertEquals(true, PasswordUtil.compare(newLogin, patchBody.hash))
        }
    }

    @Test
    fun `Test bad request`() {
        runBlocking {
            val patchResponse = adminUser.patch("${authorizedEndpoint}/user/${clientUserData.id}")

            assertEquals(HttpStatusCode.UnsupportedMediaType, patchResponse.status)
        }
    }

    @Test
    fun `Test trying to update user from different company`() {
        runBlocking {
            val patchResponse = secondAdminUser.patch("${authorizedEndpoint}/user/${clientUserData.id}") {
                setBody(updateDto)
            }

            assertEquals(HttpStatusCode.Forbidden, patchResponse.status)
        }
    }

    @Test
    fun `Test trying to update different user (for client side`() {
        runBlocking {
            val patchResponse = clientUser.patch("${authorizedEndpoint}/user/${secondAdminUserData.id}") {
                setBody(updateDto)
            }

            assertEquals(HttpStatusCode.Forbidden, patchResponse.status)
        }
    }

    @Test
    fun `Test update user with duplicate login, email or phone`() {
        runBlocking {
            val patchResponse = adminUser.patch("${authorizedEndpoint}/user/${clientUserData.id}") {
                setBody(
                    UserUpdateDto(
                        login = adminUserData.login,
                        email = adminUserData.login,
                        phone = adminUserData.login,
                        avatar = null,
                        password = "",
                        fullname = ""
                    )
                )
            }

            val duplicateEntryException = DuplicateEntryException()
            assertEquals(HttpStatusCode(duplicateEntryException.status, duplicateEntryException.statusDescription), patchResponse.status)
        }
    }

    @Test
    fun `Test user not found`() {
        runBlocking {
            val patchResponse = adminUser.patch("${authorizedEndpoint}/user/0") {
                setBody(updateDto)
            }

            assertEquals(HttpStatusCode.NotFound, patchResponse.status)
        }
    }
}