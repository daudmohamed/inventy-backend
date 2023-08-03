package com.inventy.config

import io.ktor.server.application.*
import com.auth0.jwk.JwkProviderBuilder
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.concurrent.TimeUnit

fun Application.configureAuth() {
    fun validateCreds(credential: JWTCredential): JWTPrincipal? {
        println(credential)
        val containsAudience = credential.payload.audience.contains(environment.config.property("auth0.audience").getString())

        if (containsAudience) {
            return JWTPrincipal(credential.payload)
        }

        return null
    }

    val issuer = environment.config.property("auth0.issuer").getString()
    val jwkProvider = JwkProviderBuilder(issuer)
        .cached(10, 24, TimeUnit.HOURS)
        .rateLimited(10, 1, TimeUnit.MINUTES)
        .build()

    install(Authentication) {
        jwt("auth0") {
            verifier(jwkProvider, issuer)
            validate { credential -> validateCreds(credential) }
        }
    }
}