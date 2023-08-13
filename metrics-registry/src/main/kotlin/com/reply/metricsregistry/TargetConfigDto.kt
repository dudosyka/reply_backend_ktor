package com.reply.metricsregistry

import kotlinx.serialization.Serializable

@Serializable
data class TargetConfigDto (
    val labels: Map<String, String>,
    var targets: List<String>
)