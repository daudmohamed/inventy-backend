package com.inventy

import com.inventy.client.BarcodeLookupClient
import io.ktor.server.application.*
import com.inventy.plugins.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    //configureMonitoring()
    configureSerialization()
    val database = configureDatabase()
    val barcodeLookupClient = BarcodeLookupClient(environment.config.property("barcode-lookup.api-key").getString())
    configureRouting(database,barcodeLookupClient)
}
