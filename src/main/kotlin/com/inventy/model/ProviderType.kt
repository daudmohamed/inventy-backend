package com.inventy.model

import kotlinx.serialization.SerialName

enum class ProviderType (val id: Int) {
    @SerialName("auth0")
    AUTH0(0),
    @SerialName("google-oauth2")
    GOOGLE(1),
    @SerialName("facebook")
    FACEBOOK(2);

    companion object {
        fun fromId(id: Int): ProviderType {
            values().forEach {
                if (it.id == id) {
                    return it
                }
            }
            throw IllegalArgumentException("No AuthProvider with id $id found")
        }
    }

}