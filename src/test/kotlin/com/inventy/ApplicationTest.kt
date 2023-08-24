package com.inventy

import com.inventy.client.Auth0Client
import com.inventy.client.BarcodeLookupClient
import com.inventy.route.configureRouting
import com.inventy.dto.ProviderDTO
import com.inventy.dto.UserDTO
import com.inventy.model.ProviderType
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.http.*
import com.inventy.plugins.*
import com.inventy.repository.UserRepository
import kotlinx.coroutines.runBlocking

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            DatabaseFactory(
                dbHost = environment.config.property("database.host").getString(),
                dbPort = environment.config.property("database.port").getString(),
                dbUser = environment.config.property("database.user").getString(),
                dbPassword = environment.config.property("database.password").getString(),
                databaseName = environment.config.property("database.databaseName").getString(),
                embedded = environment.config.property("database.embedded").getString().toBoolean(),
            ).init()
            val barcodeApiKey = environment.config.property("barcode-lookup.api-key").getString()
            val auth0Host = environment.config.property("auth0.issuer").getString()
            val barcodeLookupClient = BarcodeLookupClient(barcodeApiKey)
            val auth0Client = Auth0Client(auth0Host)
            configureRouting(barcodeLookupClient, auth0Client, testing = true)
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
