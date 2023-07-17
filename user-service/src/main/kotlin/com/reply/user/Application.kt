package com.reply.user

import com.reply.libs.config.ApiConfig
import com.reply.libs.database.models.CompanyModel
import com.reply.libs.utils.database.DatabaseConnector
import com.reply.libs.utils.kodein.bindSingleton
import com.reply.libs.utils.kodein.kodeinApplication
import com.reply.libs.database.models.FileModel
import com.reply.libs.database.models.RoleModel
import com.reply.libs.database.models.UserModel
import com.reply.libs.plugins.*
import com.reply.libs.plugins.consul.ConsulServer
import com.reply.libs.consul.FileServiceClient
import com.reply.user.controller.AuthController
import com.reply.user.controller.CheckTokenController
import com.reply.user.controller.CompanyController
import com.reply.user.service.AuthService
import com.reply.user.service.CompanyService
import com.reply.user.service.UserService
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.logging.*

fun main() = EngineMain.main(Array<String>(0) { "" })

fun Application.module() {
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

        //Services
        bindSingleton { AuthService(it) }
        bindSingleton { CompanyService(it) }
        bindSingleton { UserService(it) }

        //Logger
        bindSingleton { KtorSimpleLogger("UserService") }

    }
    DatabaseConnector(UserModel, RoleModel, FileModel, CompanyModel) {}
    install(ConsulServer) {
        serviceName = ApiConfig.userServiceName
        host = "localhost"
        port = (this@module.environment as ApplicationEngineEnvironment).connectors[0].port
        consulUrl = "http://localhost:8500"
        config {
            port = (environment as ApplicationEngineEnvironment).connectors.first().port
        }
        registrationConfig {
//            check(Registration.RegCheck.http("$host:$port${ApiConfig().openEndpoint}/user/health", 120))
        }
    }
//    environment.monitor.subscribe(ServerReady) { app ->
//    }
}
