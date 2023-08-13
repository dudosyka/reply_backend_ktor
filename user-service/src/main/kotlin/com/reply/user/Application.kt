package com.reply.user

import com.orbitz.consul.model.agent.Registration
import com.reply.libs.config.ApiConfig
import com.reply.libs.consul.FileServiceClient
import com.reply.libs.database.models.*
import com.reply.libs.plugins.*
import com.reply.libs.plugins.consul.ConsulServer
import com.reply.libs.utils.consul.FreePortGenerator
import com.reply.libs.utils.consul.health.HealthCheckController
import com.reply.libs.utils.consul.health.ServiceStatus
import com.reply.libs.utils.consul.health.Status
import com.reply.libs.utils.database.DatabaseConnector
import com.reply.libs.utils.kodein.bindSingleton
import com.reply.libs.utils.kodein.kodeinApplication
import com.reply.user.controller.*
import com.reply.user.service.AuthService
import com.reply.user.service.CompanyService
import com.reply.user.service.GroupService
import com.reply.user.service.UserService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.routing.*
import io.ktor.util.logging.*

fun main() {
    embeddedServer(Netty, port = FreePortGenerator(), host = "127.0.0.1") {
        configureSecurity()
        configureHTTP()
        configureMonitoring()
        configureSerialization()
        configureValidation()
        responseFilter()
        kodeinApplication {
            //Consul
            bindSingleton { FileServiceClient(it) }

            //Controllers
            bindSingleton { AuthController(it) }
            bindSingleton { CheckTokenController(it) }
            bindSingleton { CompanyController(it) }
            bindSingleton { GroupController(it) }
            bindSingleton { UserController(it) }
            bindSingleton { HealthCheckController(it) { ServiceStatus(Status.OK, "Alive") } }

            //Services
            bindSingleton { AuthService(it) }
            bindSingleton { CompanyService(it) }
            bindSingleton { GroupService(it) }
            bindSingleton { UserService(it) }

            //Logger
            bindSingleton { KtorSimpleLogger("UserService") }

        }
        DatabaseConnector(UserModel, RoleModel, FileModel, CompanyModel, GroupModel, GroupUsersModel) {}
        routing {
            post("/admin/signup") {}
        }
        environment.monitor.subscribe(ServerReady) { application ->
            application.apply {
                install(ConsulServer) {
                    serviceName = ApiConfig.userServiceName
                    host = "localhost"
                    port = FreePortGenerator()
                    consulUrl = "http://localhost:8500"
                    registrationConfig {
                        check(Registration.RegCheck.http("http://host.docker.internal:$port${ApiConfig.openEndpoint}/health", 10, 10))
                    }
                }
            }
        }
    }.start(wait = true)
}
