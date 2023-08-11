package com.reply.libs.utils.database

import com.reply.libs.config.DatabaseConfig
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseConnector(vararg tables: Table, initializer: Transaction.() -> Unit) {
    init {
        TransactionManager.defaultDatabase = Database.connect(DatabaseConfig.url, driver = DatabaseConfig.driver,
            user = DatabaseConfig.user, password = DatabaseConfig.password)

        transaction {
            tables.forEach {
                SchemaUtils.create(it)
            }
            initializer()
        }
    }
}