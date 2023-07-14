package com.reply.libs.config.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

class DatabaseConnector(vararg tables: BaseIntIdTable, initializer: Transaction.() -> Unit) {
    init {
        val config = DatabaseConfig()
        TransactionManager.defaultDatabase = Database.connect(config.url, driver = config.driver,
            user = config.user, password = config.password)
        transaction {
            tables.forEach {
                SchemaUtils.create(it)
            }
            initializer()
        }
    }
}