package com.inventy.dto

import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    override val id: Long? = 0,
    val firstName: String,
    val lastName: String,
    val email: String,
    val imageUrl: String,
    val providers: List<ProviderDTO>
) : DTO

