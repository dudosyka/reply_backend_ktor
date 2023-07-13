package com.reply.libs.consul.client

import com.orbitz.consul.Consul
import com.reply.libs.consul.server.LoadBalancer
import com.reply.libs.consul.server.takeFirst
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.util.*
import org.slf4j.LoggerFactory
import kotlin.properties.Delegates

@Suppress("MemberVisibilityCanBePrivate")
class ConsulClientFeature(private val config: Config) {
    class Config {
        private var loadBalancer: LoadBalancer = takeFirst()
        private var config: Consul.Builder.() -> Unit = {}

        var consulUrl by Delegates.notNull<String>()
        var serviceName: String? = null

        fun loadBalancer(loadBalancer: LoadBalancer) {
            this.loadBalancer = loadBalancer
        }

        fun config(config: Consul.Builder.() -> Unit) {
            this.config = config
        }

        internal operator fun component1() = loadBalancer
        internal operator fun component2() = config
        internal operator fun component3() = consulUrl
        internal operator fun component4() = serviceName
    }

    companion object Feature : HttpClientPlugin<Config, ConsulClientFeature> {
        private val logger = LoggerFactory.getLogger(ConsulClientFeature::class.java)

        override val key = AttributeKey<ConsulClientFeature>("ConsulClient")

        override fun prepare(block: Config.() -> Unit) = ConsulClientFeature(Config().apply(block))

        override fun install(plugin: ConsulClientFeature, scope: HttpClient) {
            val (loadBalancer, consulConfig, consulUrl, possibleServiceName) = plugin.config

            scope.requestPipeline.intercept(HttpRequestPipeline.Render) {
                val serviceName = possibleServiceName ?: context.url.host

                val consulClient = Consul.builder().withUrl(consulUrl).apply(consulConfig).build()
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