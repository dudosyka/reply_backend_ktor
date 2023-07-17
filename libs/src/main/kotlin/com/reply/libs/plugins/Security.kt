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

    val jwtVerifier = JWT
        .require(Algorithm.HMAC256(JwtConfig.secret))
        .withIssuer(JwtConfig.domain)
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
                val role = credential.payload.claims["role"]?.asString()?.toInt() ?: 0

                if (role == RBACConfig.ADMIN.roleId) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
        jwt(RBACConfig.CLIENT.toString()) {
            verifier(jwtVerifier)
            validate { credential ->
                val role = credential.payload.claims["role"]?.asString()?.toInt() ?: 0

                if (role == RBACConfig.CLIENT.roleId) {
                    JWTPrincipal(credential.payload)
                } else null
            }
        }
    }
}

fun createToken(claims: MutableMap<String, String>): String {
    return JWT.create()
        .withIssuer(JwtConfig.domain)
        .withExpiresAt(Date(System.currentTimeMillis() + JwtConfig.expiration))
        .apply {
            claims.forEach {
                withClaim(it.key, it.value)
            }
        }.sign(Algorithm.HMAC256(JwtConfig.secret))
}
