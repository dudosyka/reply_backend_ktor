package com.reply.libs.consul.server

import com.orbitz.consul.Consul
import com.orbitz.consul.model.agent.ImmutableRegistration
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.util.*
import kotlin.properties.Delegates

class ConsulFeature private constructor(
    config: Config
) {
    init {
        val (serviceName, host, port, consulUrl, configConsul, registrationConfig) = config
        val consulClient = Consul.builder()
            .withUrl(consulUrl)
            .apply(configConsul)
            .build()

        val service = ImmutableRegistration.builder()
            .id("$serviceName-$port")
            .name(serviceName)
            .address(host)
            .port(port)
            .addChecks()
            .apply(registrationConfig)
            .build()

        consulClient.agentClient().register(service)
    }

    class Config(
        var serviceName: String,
        var host: String,
        var port: Int
    ) {
        private var configConsul: Consul.Builder.() -> Unit = {}
        private var registrationConfig: ImmutableRegistration.Builder.() -> Unit = {}
        var consulUrl by Delegates.notNull<String>()

        fun config(configConsul: Consul.Builder.() -> Unit) {
            this.configConsul = configConsul
        }

        fun registrationConfig(registrationConfig: ImmutableRegistration.Builder.() -> Unit) {
            this.registrationConfig = registrationConfig
        }

        internal operator fun component1() = serviceName
        internal operator fun component2() = host
        internal operator fun component3() = port
        internal operator fun component4() = consulUrl
        internal operator fun component5() = configConsul
        internal operator fun component6() = registrationConfig
    }

    companion object Feature : BaseApplicationPlugin<Application, Config, ConsulFeature> {
        override val key: AttributeKey<ConsulFeature> = AttributeKey("Consul")

        override fun install(pipeline: Application, configure: Config.() -> Unit): ConsulFeature {
            @Suppress("EXPERIMENTAL_API_USAGE")
            fun prop(name: String) = pipeline.environment.config.propertyOrNull(name)?.getString()

            val connector = if (pipeline.environment is ApplicationEngineEnvironment) {
                (pipeline.environment as ApplicationEngineEnvironment).connectors.getOrNull(0)
            } else {
                null
            }

            val name = prop("ktor.application.id") ?: "application"
            val host = connector?.host ?: "localhost"
            val port = connector?.port ?: prop("ktor.deployment.port")?.toInt() ?: 80

            return ConsulFeature(Config(name, host, port).apply(configure))
        }
    }
}