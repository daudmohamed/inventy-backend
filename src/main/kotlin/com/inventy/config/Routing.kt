package com.inventy.plugins

import com.inventy.client.BarcodeLookupClient
import com.inventy.routes.configureItemRoute
import io.ktor.server.routing.*
import io.ktor.server.response.*
import io.ktor.server.resources.*
import io.ktor.resources.*
import io.ktor.server.resources.Resources
import kotlinx.serialization.Serializable
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureRouting(
    database: Database,
    barcodeLookupClient: BarcodeLookupClient
) {
    install(Resources)
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
