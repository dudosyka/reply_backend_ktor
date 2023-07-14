package com.reply.user

import com.reply.libs.config.database.DatabaseConnector
import com.reply.libs.config.kodein.bindSingleton
import com.reply.libs.config.kodein.kodeinApplication
import com.reply.libs.database.models.FileModel
import com.reply.libs.database.models.RoleModel
import com.reply.libs.database.models.UserModel
import com.reply.libs.plugins.*
import com.reply.libs.plugins.consul.ConsulServer
import com.reply.user.controller.AuthController
import com.reply.user.service.AuthService
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
        bindSingleton { AuthController(it) }
        bindSingleton { AuthService() }
        bindSingleton { KtorSimpleLogger("UserService") }
    }
    DatabaseConnector(UserModel, RoleModel, FileModel) {
//        val role = RoleDao.new {
//            name = "Admin"
//            description = "Admin role"
//        }
//        val file = FileDao.new {
//            path = "/Users/dudosyka/path"
//        }
//        UserDao.new {
//            login = "dudosyka"
//            hash = "hash"
//            email = "a.hatson@ya.ru"
//            this.role = role
//            avatar = file
//        }
    }
    install(ConsulServer) {
        serviceName = "user"
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
