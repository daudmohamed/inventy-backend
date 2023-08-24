package com.inventy.client

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.request.headers
import io.ktor.http.*

class Auth0Client(val host: String) {
    companion object {
        val client = HttpClient {
            defaultClient()
        }
    }

    suspend fun getUserInfo(accessToken: String?): Any {
        if (accessToken == null) {
            throw IllegalArgumentException("No access token provided")
        }
        return client.get("$host/userinfo") {
            headers {
                append(HttpHeaders.Authorization, accessToken)
            }
        }.body()
    }
}