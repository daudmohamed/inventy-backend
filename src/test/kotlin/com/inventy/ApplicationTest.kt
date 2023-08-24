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
            val database = configureDatabase()
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

    @Test
    fun testUsers() = testApplication {
        var userRepository: UserRepository? = null
        application {
            val database = configureDatabase()
            val barcodeApiKey = environment.config.property("barcode-lookup.api-key").getString()
            val auth0Host = environment.config.property("auth0.issuer").getString()
            val barcodeLookupClient = BarcodeLookupClient(barcodeApiKey)
            val auth0Client = Auth0Client(auth0Host)
            configureRouting(barcodeLookupClient, auth0Client, testing = true)
            userRepository = UserRepository()
            assertNotNull(userRepository)
            runBlocking {
                val userDTO = UserDTO(
                    firstName = "Daud",
                    lastName = "Mohamed",
                    email = "rando",
                    imageUrl = "radno",
                    providers = listOf(
                        ProviderDTO(
                            providerType = ProviderType.AUTH0,
                            providerId = "auth0"
                        ),
                        ProviderDTO(
                            providerType = ProviderType.GOOGLE,
                            providerId = "google"
                        ),
                    )
                )
                userRepository!!.create(
                    userDTO = userDTO
                )
                val userDTOS = userRepository!!.list()
                println(userDTOS)
                val byEmail = userRepository!!.readByEmail(userDTO.email)
                assertNotNull(byEmail)
                assertEquals(byEmail.firstName, "daud")
                assertEquals(byEmail.providers.size, 2)
            }
        }



    }
}
