package com.reply.libs.plugins

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
import com.reply.libs.config.JwtConfig
import com.reply.libs.config.RBACConfig
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.*


fun Application.configureSecurity() {
    val jwtConfig = JwtConfig()

    val jwtVerifier = JWT
        .require(Algorithm.HMAC256(jwtConfig.secret))
        .withIssuer(jwtConfig.domain)
        .build()

    authentication {
        jwt(RBACConfig.AUTHORIZED.toString()) {
            verifier(jwtVerifier)
            validate {
                JWTPrincipal(it.payload)
            }
        }
        jwt(RBACConfig.ADMIN.toString()) {
            verifier(jwtVerifier)
            validate { credential ->
                val claims = credential.payload.claims
                if ((claims["role"]?.asInt() ?: 0) == 1) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
        jwt(RBACConfig.CLIENT.toString()) {
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
        .withIssuer(jwtConfig.domain)
        .withExpiresAt(Date(System.currentTimeMillis() + jwtConfig.expiration))
        .apply {
            claims.forEach {
                withClaim(it.key, it.value)
            }
        }.sign(Algorithm.HMAC256(jwtConfig.secret))
}
