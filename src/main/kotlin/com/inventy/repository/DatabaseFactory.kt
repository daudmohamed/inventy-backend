package com.inventy.plugins

import com.zaxxer.hikari.HikariDataSource
import java.util.concurrent.TimeUnit.MINUTES
import org.jetbrains.exposed.sql.*
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import kotlinx.coroutines.*
import java.sql.*
import io.ktor.server.application.*
import io.ktor.server.config.*
import io.ktor.server.config.yaml.*
import io.ktor.server.routing.*
import org.flywaydb.core.Flyway
import org.postgresql.ds.PGSimpleDataSource
import org.h2.jdbcx.JdbcDataSource
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction


/**
 * Makes a connection to a Postgres database.
 *
 * In order to connect to your running Postgres process,
 * please specify the following parameters in your configuration file:
 * - postgres.url -- Url of your running database process.
 * - postgres.user -- Username for database connection
 * - postgres.password -- Password for database connection
 *
 * If you don't have a database process running yet, you may need to [download]((https://www.postgresql.org/download/))
 * and install Postgres and follow the instructions [here](https://postgresapp.com/).
 * Then, you would be able to edit your url,  which is usually "jdbc:postgresql://host:port/database", as well as
 * user and password values.
 *
 *
 * @param embedded -- if [true] defaults to an embedded database for tests that runs locally in the same process.
 * In this case you don't have to provide any parameters in configuration file, and you don't have to run a process.
 *
 * @return [Connection] that represent connection to the database. Please, don't forget to close this connection when
 * your application shuts down by calling [Connection.close]
 * */
fun Application.configureDatabase(): Database {
    Class.forName("org.postgresql.Driver")
    val embedded = environment.config.property("database.embedded").getString().toBoolean()
    if (embedded) {
        return Database.connect(
            url = "jdbc:h2:mem:test;DB_CLOSE_DELAY=-1",
            user = "root",
            driver = "org.h2.Driver",
            password = ""
        )
    } else {
        val url = environment.config.property("database.url").getString()
        val user = environment.config.property("database.user").getString()
        val driver = environment.config.property("database.driver").getString()
        val password = environment.config.property("database.password").getString()

        return Database.connect(
            url = url,
            user = user,
            driver = driver,
            password = password
        )
    }
}

class DatabaseFactory(
    private val dbHost: String,
    private val dbPort: String,
    private val dbUser: String,
    private val dbPassword: String,
    private val databaseName: String,
    private val embedded: Boolean = false
    ) {

    companion object {
        suspend fun <T> dbQuery(block: suspend () -> T): T =
            newSuspendedTransaction(Dispatchers.IO) { block() }
    }

    fun init() {
        Database.connect(hikari())
        val flyway = Flyway.configure()
            .dataSource(hikari())
            .load()
        flyway.migrate()
    }

    private fun hikari(): HikariDataSource {
        if (embedded) {
            return HikariDataSource().apply {
                dataSourceClassName = JdbcDataSource::class.qualifiedName
                addDataSourceProperty("url", "jdbc:h2:mem:inventy;DB_CLOSE_DELAY=-1")
                addDataSourceProperty("user", "root")
                addDataSourceProperty("password", "")
                maximumPoolSize = 10
                minimumIdle = 1
                idleTimeout = 100000
                connectionTimeout = 100000
                maxLifetime = MINUTES.toMillis(30)
            }
        } else {
            return HikariDataSource().apply {
                dataSourceClassName = PGSimpleDataSource::class.qualifiedName
                addDataSourceProperty("serverName", dbHost)
                addDataSourceProperty("portNumber", dbPort)
                addDataSourceProperty("user", dbUser)
                addDataSourceProperty("password", dbPassword)
                addDataSourceProperty("databaseName", databaseName)
                maximumPoolSize = 10
                minimumIdle = 1
                idleTimeout = 100000
                connectionTimeout = 100000
                maxLifetime = MINUTES.toMillis(30)
            }
        }


    }

}
