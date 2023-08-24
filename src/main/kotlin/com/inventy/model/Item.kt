package com.inventy.model

import com.inventy.dto.ItemDTO


class Item (
    override val id: Long,
    val name: String,
    val current: Int,
    val target: Int
) : Model {
    fun toDTO() = ItemDTO(id, name, current, target)
}
