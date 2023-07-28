package com.inventy

import com.inventy.client.BarcodeLookupClient
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.http.*
import com.inventy.plugins.*

class ApplicationTest {
    @Test
    fun testRoot() = testApplication {
        application {
            val database = configureDatabase()
            val barcodeLookupClient = BarcodeLookupClient(environment.config.property("barcode-lookup.api-key").getString())
            configureRouting(database, barcodeLookupClient)
        }
        client.get("/").apply {
            assertEquals(HttpStatusCode.OK, status)
            assertEquals("Hello World!", bodyAsText())
        }
    }
}
