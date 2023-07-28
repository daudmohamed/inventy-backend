package com.inventy.repository

import com.inventy.dto.ItemDTO
import com.inventy.model.Items
import kotlinx.coroutines.Dispatchers
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction

class ItemService(database: Database) {
    init {
        transaction(database) {
            SchemaUtils.create(Items)
        }
    }

    suspend fun <T> dbQuery(block: suspend () -> T): T =
        newSuspendedTransaction(Dispatchers.IO) { block() }

    suspend fun create(itemDTO: ItemDTO): Long = dbQuery {
        Items.insert {
            it[name] = itemDTO.name
            it[current] = itemDTO.current
            it[target] = itemDTO.target
        }[Items.id]
    }

    suspend fun read(id: Long): ItemDTO? {
        return dbQuery {
            Items.select { Items.id eq id }
                .map { ItemDTO(it[Items.id], it[Items.name], it[Items.current], it[Items.target]) }
                .singleOrNull()
        }
    }

    suspend fun update(id: Long, itemDTO: ItemDTO) {
        dbQuery {
            Items.update({ Items.id eq id }) {
                it[name] = itemDTO.name
                it[current] = itemDTO.current
            }
        }
    }

    suspend fun delete(id: Long) {
        dbQuery {
            Items.deleteWhere { Items.id.eq(id) }
        }
    }

    fun list(): List<ItemDTO> {
        return transaction {
            Items.selectAll().map {
                ItemDTO(it[Items.id], it[Items.name], it[Items.current], it[Items.target])
            }
        }
    }

    fun shoppingList(): List<ItemDTO> {
        return transaction {
            Items.select { Items.current less Items.target }.map {
                ItemDTO(it[Items.id], it[Items.name], it[Items.current], it[Items.target])
            }
        }
    }
}