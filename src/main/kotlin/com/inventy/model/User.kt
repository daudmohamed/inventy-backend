package com.inventy.model

import com.inventy.dto.UserDTO

class User (
    override val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String,
    val imageUrl: String,
    val providers: List<Provider>
) : Model {
    fun toDTO() = UserDTO(
        id,
        firstName,
        lastName,
        email,
        imageUrl,
        providers
            .map(Provider::toDTO)
    )
}
