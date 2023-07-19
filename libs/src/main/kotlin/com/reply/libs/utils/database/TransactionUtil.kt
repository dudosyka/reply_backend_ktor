package com.reply.libs.utils.database

import org.jetbrains.exposed.exceptions.ExposedSQLException
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.experimental.suspendedTransaction

interface TransactionalService {
    suspend fun <T> transaction(statements: suspend Transaction.() -> T) = TransactionManager.currentOrNew(-1).suspendedTransaction {
        try {
            statements()
        } catch (e: Exception) {
            throw e
        } catch (e: ExposedSQLException) {
            throw e
        }
    }


}