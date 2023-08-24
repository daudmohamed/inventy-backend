package com.inventy.model

import com.inventy.dto.ProviderDTO

class Provider(
    override val id: Long,
    val userId: Long,
    val providerType: Int,
    private val providerId: String,
) : Model {
    fun toDTO() = ProviderDTO(id, ProviderType.fromId(providerType), providerId)
}
