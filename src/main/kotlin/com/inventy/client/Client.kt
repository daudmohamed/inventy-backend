package com.inventy.client

import io.ktor.client.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.logging.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json

fun HttpClientConfig<*>.defaultClient() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
        })
    }

    install(ContentEncoding){
        gzip()
        deflate()
    }

    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
        filter { request ->
            request.url.host.contains("ktor-avvik-api")
        }
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }
    // Add all the common configuration here.
}
