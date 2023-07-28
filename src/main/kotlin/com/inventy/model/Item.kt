package com.inventy.model

import org.jetbrains.exposed.sql.Table

object Items : Table() {
    val id = long("id").autoIncrement()
    val name = varchar("title", 128)
    val current = integer("current").default(1)
    val target = integer("target").default(2)

    override val primaryKey = PrimaryKey(id)
}