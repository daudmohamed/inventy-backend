package com.inventy

import com.inventy.client.Auth0Client
import com.inventy.client.BarcodeLookupClient
import com.inventy.config.configureAuth
import com.inventy.route.configureRouting
import io.ktor.server.application.*
import com.inventy.plugins.*
import com.typesafe.config.ConfigFactory
import io.ktor.server.config.*

fun main(args: Array<String>): Unit =
    io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // application.conf references the main function. This annotation prevents the IDE from marking it as unused.
fun Application.module() {
    //configureMonitoring()
    configureSerialization()
    configureAuth()
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
    configureRouting(barcodeLookupClient, auth0Client)
}

