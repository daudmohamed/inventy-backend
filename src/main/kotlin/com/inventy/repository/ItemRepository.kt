package com.inventy.repository

import com.inventy.dto.ItemDTO
import com.inventy.model.Item
import com.inventy.plugins.DatabaseFactory.Companion.dbQuery
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class ItemRepository(userId: Long) : UserOwnedRepository(userId){

    private object ItemTable : LongIdTable() {
        val name = varchar("name", 128)
        val userId = reference("user_id", UserRepository.UserTable.id)
        val current = integer("current").default(1)
        val target = integer("target").default(2)

        fun toModel(it: ResultRow) = Item(
            it[id].value,
            it[name],
            it[current],
            it[target]
        )
    }

    suspend fun create(item: ItemDTO): Long = dbQuery {
        ItemTable.insertAndGetId {
            it[name] = item.name
            it[userId] = this@ItemRepository.userId
            it[current] = item.current
            it[target] = item.target
        }.value
    }

    suspend fun read(id: Long): Item? {
        return dbQuery {
            ItemTable.select { ItemTable.id eq id }
                .andWhere { ItemTable.userId eq userId  }
                .map(ItemTable::toModel)
                .singleOrNull()
        }
    }

    suspend fun update(id: Long, itemDTO: ItemDTO) {
        dbQuery {
            ItemTable.update(
                { ItemTable.id eq id and (ItemTable.userId eq this@ItemRepository.userId) }
            ) {
                it[name] = itemDTO.name
                it[userId] = userId
                it[current] = itemDTO.current
            }
        }
    }

    suspend fun delete(id: Long) {
        dbQuery {
            ItemTable.deleteWhere { ItemTable.id.eq(id) and (userId eq this@ItemRepository.userId)}

        }
    }

    fun list(): List<Item> {
        return transaction {
            ItemTable.select { ItemTable.userId eq userId }
                .map(ItemTable::toModel)
        }
    }

    fun shoppingList(): List<Item> {
        return transaction {
            ItemTable.select {  ItemTable.userId eq userId }
                .andWhere {  ItemTable.current less ItemTable.target }
                .map(ItemTable::toModel)
        }
    }
}