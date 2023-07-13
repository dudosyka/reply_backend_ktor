package com.reply.libs.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.reply.libs.config.JwtConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*


fun Application.configureSecurity() {
    // Please read the jwt property from the config file if you are using EngineMain
    val jwtConfig = JwtConfig()

    val jwtVerifier = JWT
        .require(Algorithm.HMAC256(jwtConfig.secret))
        .withAudience(jwtConfig.audience)
        .withIssuer(jwtConfig.domain)
        .build()

    authentication {
        jwt("Authorized") {
            realm = jwtConfig.realm
            verifier(jwtVerifier)
            validate { JWTPrincipal(it.payload) }
        }
        jwt("Admin role") {
            realm = jwtConfig.realm
            verifier(jwtVerifier)
            validate { credential ->
                val claims = credential.payload.claims
                if ((claims["role"]?.asInt() ?: 0) == 1) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
        jwt("User role") {
            realm = jwtConfig.realm
            verifier(jwtVerifier)
            validate { credential ->
                val claims = credential.payload.claims
                if ((claims["role"]?.asInt() ?: 0) == 2) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}

fun createToken(claims: MutableMap<String, String>): String {
    val jwtConfig = JwtConfig()
    return JWT.create()
        .withAudience(jwtConfig.audience)
        .withIssuer(jwtConfig.domain)
        .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.expiration)).apply {
            claims.forEach {
                withClaim(it.key, it.value)
            }
        }.sign(Algorithm.HMAC256(jwtConfig.secret))
}
