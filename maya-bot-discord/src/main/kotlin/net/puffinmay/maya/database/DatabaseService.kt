package net.puffinmay.maya.database

import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import net.puffinmay.maya.database.table.AfkStatus
import net.puffinmay.maya.database.table.Blacklist
import net.puffinmay.maya.database.table.Categories
import net.puffinmay.maya.database.table.ConnectionInvites
import net.puffinmay.maya.database.table.Connections
import net.puffinmay.maya.database.table.ForwardMessages
import net.puffinmay.maya.database.table.Guilds
import net.puffinmay.maya.database.table.MessageLikes
import net.puffinmay.maya.database.table.Messages
import net.puffinmay.maya.database.table.ModerationRules
import net.puffinmay.maya.database.table.Notifications
import net.puffinmay.maya.database.table.Partners
import net.puffinmay.maya.database.table.Premiums
import net.puffinmay.maya.database.table.SavedMessages
import net.puffinmay.maya.database.table.StaffMembers
import net.puffinmay.maya.database.table.Testimonials
import net.puffinmay.maya.database.table.Users
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

data class PostgresConfig(
    val url: String,
    val username: String,
    val password: String
)

class DatabaseService {
    private var hikari: HikariDataSource? = null

    fun connect(postgres: PostgresConfig) {
        val config = HikariConfig().apply {
            jdbcUrl = "jdbc:${postgres.url}"
            driverClassName = "org.postgresql.Driver"
            username = postgres.username
            password = postgres.password
            maximumPoolSize = 8
        }

        hikari = HikariDataSource(config)
        Database.connect(hikari!!)

        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                Guilds,
                Connections,
                ConnectionInvites,
                ModerationRules,
                Categories,
                Messages,
                MessageLikes,
                SavedMessages,
                ForwardMessages,
                Users,
                Premiums,
                Partners,
                StaffMembers,
                AfkStatus,
                Blacklist,
                Notifications,
                Testimonials
            )
        }
    }

    fun close() {
        hikari?.close()
        hikari = null
    }
}