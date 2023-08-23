package com.inventy.config

import io.ktor.server.application.*
import com.auth0.jwk.JwkProviderBuilder
import com.auth0.jwt.interfaces.Payload
import com.inventy.repository.UserRepository
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import java.util.concurrent.TimeUnit

class CustomPrincipal(payload: Payload, val userId: Long): Principal, JWTPayloadHolder(payload)

fun Application.configureAuth() {
    suspend fun validateCreds(credential: JWTCredential): CustomPrincipal? {
        val containsAudience = credential.payload.audience.contains(environment.config.property("auth0.audience").getString())

        if (containsAudience) {
            val userRepository = UserRepository()

            val subject = credential.payload.subject
            val providerId = subject.split("|")[1]
            val provider = userRepository.findProviderById(providerId) ?: throw Exception("Provider not found")
            return CustomPrincipal(credential.payload, provider.userId)
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
        basic("basic-auth0") {
            realm = "Used by auth0 for creating users"
            validate { credentials ->
                if (credentials.name == "inventy-auth0" && credentials.password == "password") {
                    UserIdPrincipal(credentials.name)
                } else {
                    null
                }
            }
        }
    }
}