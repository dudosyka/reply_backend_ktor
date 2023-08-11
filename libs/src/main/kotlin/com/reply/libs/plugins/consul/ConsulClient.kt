package com.reply.libs.plugins.consul

import com.orbitz.consul.Consul
import com.reply.libs.utils.consul.LoadBalancer
import com.reply.libs.utils.consul.takeFirst
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.util.*
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

@Suppress("MemberVisibilityCanBePrivate")
class ConsulClient(private val config: Config) {
    class Config {
        private var loadBalancer: LoadBalancer = takeFirst()
        private var config: Consul.Builder.() -> Unit = {}

        var serviceName: String? = null
        var consulClient: Consul by Delegates.notNull()

        fun loadBalancer(loadBalancer: LoadBalancer) {
            this.loadBalancer = loadBalancer
        }

        fun config(config: Consul.Builder.() -> Unit) {
            this.config = config
        }

        internal operator fun component1() = loadBalancer
        internal operator fun component2() = serviceName
        internal operator fun component3() = consulClient
    }

    companion object Feature : HttpClientPlugin<Config, ConsulClient> {
        private val logger = LoggerFactory.getLogger(ConsulClient::class.java)

        override val key = AttributeKey<ConsulClient>("ConsulClient")

        override fun prepare(block: Config.() -> Unit) = ConsulClient(Config().apply(block))

        override fun install(plugin: ConsulClient, scope: HttpClient) {
            val (loadBalancer, possibleServiceName, consulClient) = plugin.config

            scope.requestPipeline.intercept(HttpRequestPipeline.Render) {
                val serviceName = possibleServiceName ?: context.url.host

                val nodes = consulClient.healthClient().getHealthyServiceInstances(serviceName).response

                val selectedNode = checkNotNull(nodes.loadBalancer()) {
                    "Impossible to find available nodes of the $serviceName"
                }

                val serviceHost = extractHost(selectedNode.service.address)

                context.url.host = serviceHost
                context.url.port = selectedNode.service.port

                logger.trace("Calling ${selectedNode.service.id}: ${context.url.buildString()}")
            }
        }

        fun extractHost(address: String): String =
            address.removePrefix("https://")
                .removePrefix("http://")
    }
}