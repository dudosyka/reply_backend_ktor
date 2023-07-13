package com.reply.user.consul

import com.reply.libs.consul.ConsulClientFeature
import com.reply.libs.consul.roundRobin
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.client.plugins.contentnegotiation.*

class ConsulClient {
    companion object {
        val client = HttpClient(Apache) {
            install(ConsulClientFeature) {
                consulUrl = "http://localhost:8500"
                serviceName = "MyService"
//                loadBalancer { // this: List<ServiceHealth>
//                    // Your implementation:
//                    // Return one of the ServiceHealth
//                    // by default:
//                    getOrNull(0)
//                }
                // Also, one more load balancer implementation available:
                loadBalancer(roundRobin())
                config { // this: Consul.Builder
                    // ...
                }
            }
            install(ContentNegotiation)
        }
    }
}