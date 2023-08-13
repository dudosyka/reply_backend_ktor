package com.reply.libs.utils.consul.health

import kotlinx.serialization.Serializable

enum class Status {
    OK, WARN, CRITICAL
}

@Serializable
data class ServiceStatus(
    val status: Status,
    val msg: String
)
