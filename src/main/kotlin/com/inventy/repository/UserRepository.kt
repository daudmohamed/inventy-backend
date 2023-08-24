package com.inventy.repository

import com.inventy.dto.ProviderDTO
import com.inventy.dto.UserDTO
import com.inventy.model.*
import com.inventy.plugins.DatabaseFactory.Companion.dbQuery
import org.jetbrains.exposed.dao.id.LongIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

class UserRepository {

    private object ProviderTable: LongIdTable() {
        val userId = reference("user_id", UserTable.id)
        val providerType = integer("provider").default(0)
        val providerId = varchar("provider_id", 128)

        fun toModel(it: ResultRow) = Provider(
            it[id].value,
            it[userId].value,
            it[providerType],
            it[providerId]
        )
    }

    object UserTable : LongIdTable() {
        val firstName = varchar("first_name", 128)
        val lastName = varchar("last_name", 128)
        val email = varchar("email", 128)
        val imageUrl = varchar("image_url", 256)

        fun toModel(it: ResultRow, providers: List<Provider>) = User(
            it[id].value,
            it[firstName],
            it[lastName],
            it[email],
            it[imageUrl],
            providers
        )
    }

    suspend fun create(userDTO: UserDTO): Long = dbQuery {
        val id = UserTable.insertAndGetId {
            it[firstName] = userDTO.firstName
            it[lastName] = userDTO.lastName
            it[email] = userDTO.email
            it[imageUrl] = userDTO.imageUrl
        }.value

        ProviderTable.batchInsert(userDTO.providers) { (_, providerType, providerId) ->
            this[ProviderTable.userId] = id
            this[ProviderTable.providerId] = providerId
            this[ProviderTable.providerType] = providerType.id

        }

        return@dbQuery id
    }

    suspend fun read(providerId: String): User? {
        return dbQuery {
            UserTable
                .select(
                ProviderTable.providerId.eq(providerId) and (ProviderTable.userId eq UserTable.id)
            ).map { row ->
                    UserTable.toModel(
                        row,
                        ProviderTable.select(where = ProviderTable.userId eq row[UserTable.id])
                            .map(ProviderTable::toModel).toList()
                    )
            }.singleOrNull()
        }
    }
    fun list(): List<User> {
        return transaction {
            UserTable
                .selectAll()
                .distinct()
                .map {row ->
                    UserTable.toModel(
                        row,
                        ProviderTable.select(where = ProviderTable.userId eq row[UserTable.id])
                            .map(ProviderTable::toModel).toList()
                    )
                }
        }
    }
    suspend fun readByEmail(email: String): User? {
        return dbQuery {
            UserTable.join(ProviderTable, JoinType.INNER, onColumn = UserTable.id, otherColumn = ProviderTable.userId, additionalConstraint = {UserTable.email eq email})
                .selectAll()
                .map {row ->
                    UserTable.toModel(
                        row,
                        ProviderTable.select(where = ProviderTable.userId eq row[UserTable.id])
                            .map(ProviderTable::toModel).toList()
                    )
                }.singleOrNull()
        }
    }

    suspend fun updateProviders(existingUserId: Long, existingProviders: List<Provider>, providers: List<ProviderDTO>) {
        providers.filter {
            existingProviders.none { provider -> provider.providerType == it.providerType.id }
        }.forEach(
            fun (provider: ProviderDTO) {
                dbQuery {
                    ProviderTable.insert {
                        it[userId] = existingUserId
                        it[providerType] = provider.providerType.id
                        it[providerId] = provider.providerId
                    }
                }
            }
        )

    }

    suspend fun findProviderById(providerId: String): Provider? {
        return dbQuery {
            ProviderTable.select(where = ProviderTable.providerId eq providerId).map(ProviderTable::toModel).singleOrNull()
        }
    }
}