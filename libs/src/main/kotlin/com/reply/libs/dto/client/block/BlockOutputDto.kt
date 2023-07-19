package com.reply.libs.dto.client.block

import com.reply.libs.dto.client.test.TestOutputDto
import kotlinx.serialization.Serializable

@Serializable
data class BlockOutputDto(
    val id : Int,
    val name : String,
    var tests :  List<TestOutputDto>
)