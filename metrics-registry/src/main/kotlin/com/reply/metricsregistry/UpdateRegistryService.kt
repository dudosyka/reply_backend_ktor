package com.reply.metricsregistry

import com.orbitz.consul.Consul
import com.orbitz.consul.HealthClient
import com.orbitz.consul.model.agent.ImmutableRegistration
import com.orbitz.consul.model.health.ServiceHealth
import kotlinx.coroutines.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File

class UpdateRegistryService() {
    private val consul: Consul = run {
        val consul = Consul.builder().withUrl("http://localhost:8500").apply {
            withReadTimeoutMillis(10001) //to bypass cache timeout exception

        }.build()
        val service = ImmutableRegistration.builder()
            .id("registry-service")
            .name("registry-service")
            .address("localhost")
            .port(8091)
            .build()

        consul.agentClient().register(service)

        consul
    }
    private val healthClient: HealthClient = consul.healthClient()
    private var services: MutableList<ServiceInstance> = mutableListOf()
    fun addService(service: ServiceInstance) {
        services.add(service)
    }
    data class ServiceInstance(
        val serviceName: String,
        val filePath: String,
        val jobName: String
    )
    suspend fun initialize() = coroutineScope {
        withContext(Dispatchers.Default) {
            services.map {
                launch {
                    while (true) {
                        fetchInstances(it.serviceName, it.filePath, it.jobName)
                        delay(5000)
                    }
                }
            }
        }
    }

    private fun processConfig(fileName: String, healthyInstances: List<ServiceHealth>, jobName: String) {
        val targets = mutableListOf<String>()
        healthyInstances.map {
            targets.add("host.docker.internal:${it.service.port}")
        }

        val file = File(fileName)
        val data = file.readText(Charsets.UTF_8)
        val config: TargetConfigDto = if (data == "") {
            TargetConfigDto(
                labels = mapOf(
                    "job" to jobName
                ),
                targets = targets.toList()
            )
        } else {
            val onUpdate = Json.decodeFromString<List<TargetConfigDto>>(data).first()
            onUpdate.targets = targets.toList()
            onUpdate
        }
        file.writeText(Json.encodeToString<List<TargetConfigDto>>(listOf(config)))
    }

    private fun fetchInstances(serviceName: String, fileName: String, jobName: String) {
        println("Fetching $serviceName instances....")
        val healthyInstances = healthClient.getHealthyServiceInstances(serviceName).response
        processConfig(fileName, healthyInstances, jobName)
    }
}