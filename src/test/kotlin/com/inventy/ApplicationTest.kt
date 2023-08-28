package com.inventy

import com.inventy.client.Auth0Client
import com.inventy.client.BarcodeLookupClient
import com.inventy.config.configureAuth
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
import io.ktor.server.config.*
import kotlinx.coroutines.runBlocking

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        environment {
            config = ApplicationConfig("application-test.yaml")
        }
        application {
            DatabaseFactory(
                dbHost = "",
                dbPort = "",
                dbUser = "",
                dbPassword = "",
                databaseName = "",
                embedded = true,
            ).init()
            val barcodeApiKey = environment.config.property("barcode-lookup.api-key").getString()
            val auth0Host = environment.config.property("auth0.issuer").getString()
            val barcodeLookupClient = BarcodeLookupClient(barcodeApiKey)
            val auth0Client = Auth0Client(auth0Host)
            configureAuth()
            configureRouting(barcodeLookupClient, auth0Client, testing = true)
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
