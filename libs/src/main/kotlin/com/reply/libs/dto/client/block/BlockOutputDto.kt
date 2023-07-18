package com.reply.libs.dto.client.block

import com.reply.libs.dto.client.test.TestOutputDto

data class BlockOutputDto(
    val id : Int,
    val name : String,
    var tests :  List<TestOutputDto>
)