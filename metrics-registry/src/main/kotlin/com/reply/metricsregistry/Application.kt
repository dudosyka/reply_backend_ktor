package com.reply.metricsregistry

import com.reply.libs.config.ApiConfig
import com.reply.libs.config.PrometheusConfig
import kotlinx.coroutines.runBlocking

fun main(): Unit = runBlocking {
    val registryService = UpdateRegistryService()
    registryService.addService(
        UpdateRegistryService.ServiceInstance(
            ApiConfig.userServiceName,
            PrometheusConfig.userServiceTargetsPath,
            PrometheusConfig.userServiceJob
        )
    )
    registryService.addService(
        UpdateRegistryService.ServiceInstance(
            ApiConfig.testServiceName,
            PrometheusConfig.testServiceTargetsPath,
            PrometheusConfig.testServiceJob
        )
    )
    registryService.addService(
        UpdateRegistryService.ServiceInstance(
            ApiConfig.fileServiceName,
            PrometheusConfig.fileServiceTargetsPath,
            PrometheusConfig.fileServiceJob
        )
    )
    registryService.initialize()
}
