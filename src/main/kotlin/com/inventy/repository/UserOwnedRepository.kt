package com.inventy.repository

import com.inventy.config.CustomPrincipal
import io.ktor.server.application.*
import io.ktor.server.auth.*

open class UserOwnedRepository(val userId: Long) {
    companion object {
        inline fun <T, reified R: UserOwnedRepository> userContext(auth: AuthenticationContext, block: (repository: R) -> T): T {
            val principal = auth.principal<CustomPrincipal>()
            val repository = R::class.java.getDeclaredConstructor(Long::class.java).newInstance(principal!!.userId)
            return block(repository)
        }
    }
}