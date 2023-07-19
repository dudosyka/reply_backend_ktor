package com.reply.libs.dto.client.test

import kotlinx.serialization.Serializable

@Serializable
data class TestCheckPermissionsDto(
    val tests : List<Int>
)
