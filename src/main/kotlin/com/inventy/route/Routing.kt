package com.inventy.route

import com.inventy.client.Auth0Client
import com.inventy.client.BarcodeLookupClient
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Application.configureRouting(
    barcodeLookupClient: BarcodeLookupClient,
    auth0Client: Auth0Client,
    testing: Boolean = false
) {
    if (testing) {
        install(Resources)
        install(CORS) {
            allowMethod(HttpMethod.Options)
            allowMethod(HttpMethod.Put)
            allowMethod(HttpMethod.Delete)
            allowMethod(HttpMethod.Patch)
            allowHeader(HttpHeaders.ContentType)
            allowHeader(HttpHeaders.Authorization)
            anyHost()
        }
    }
    configureItemRoute(barcodeLookupClient)
    configureUserRoute(auth0Client)
    configureHealth()
    routing {
        get("/") {
            call.respondText("Hello World!")
        }
    }
}

fun Application.configureHealth() {
    routing {
        get("/health") {
            call.respondText("ALIVE")
        }
    }

}
