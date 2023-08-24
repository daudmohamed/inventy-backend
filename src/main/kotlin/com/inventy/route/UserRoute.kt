package com.inventy.route

import com.inventy.client.Auth0Client
import com.inventy.dto.UserDTO
import com.inventy.repository.UserRepository
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*



fun Application.configureUserRoute(auth0Client: Auth0Client) {
    val userRepository = UserRepository()
    routing {
        authenticate ("basic-auth0") {
            post("/user") {
                val userDTO = call.receive<UserDTO>()
                val user = userRepository.readByEmail(userDTO.email)
                if (user?.id != null) {
                    userRepository.updateProviders(user.id, user.providers, userDTO.providers)
                    call.respond(HttpStatusCode.OK)
                    return@post
                } else {
                    val create = userRepository.create(userDTO)
                    call.respond(HttpStatusCode.Created, create)
                    return@post
                }
            }
        }
    }
}