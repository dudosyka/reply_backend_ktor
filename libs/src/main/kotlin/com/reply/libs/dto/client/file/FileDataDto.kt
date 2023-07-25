package com.reply.libs.dto.client.file

import kotlinx.serialization.Contextual
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class FileDataDto(
   @Contextual val file : File
)