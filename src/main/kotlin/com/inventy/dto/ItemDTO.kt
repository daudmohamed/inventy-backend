package com.inventy.dto

import kotlinx.serialization.Serializable

@Serializable
data class ItemDTO(val id: Long? = 0, val name: String, val current: Int, val target: Int)