package com.inventy.plugins

import com.inventy.client.BarcodeLookupClient
import com.inventy.routes.configureItemRoute
import io.ktor.http.*
import io.ktor.resources.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.resources.*
import io.ktor.server.resources.Resources
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.Database

fun Application.configureRouting(
    database: Database,
    barcodeLookupClient: BarcodeLookupClient
) {
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
    configureItemRoute(database, barcodeLookupClient)
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
