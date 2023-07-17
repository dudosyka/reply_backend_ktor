package com.reply.libs.utils.crud

import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.utils.database.BaseIntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.DIAware

@Suppress("UNCHECKED_CAST")
open class CrudService<Output, Create>(override val di: DI, val model: BaseIntIdTable, val dao: BaseIntEntityClass<Output, *>) : DIAware {
    open fun getOne(id: Int) = transaction {
        dao[id].toOutputDto() as Output
    }

    fun getAll(filter: SqlExpressionBuilder.() -> Op<Boolean> = { Op.nullOp() }) = transaction {
        dao.wrapQuery(model.select(filter))
    }

    fun getAllWith(filter: SqlExpressionBuilder.() -> Op<Boolean> = { Op.nullOp() }, settings: BaseIntIdTable.() -> Unit) = transaction {
        dao.wrapQuery(model.apply(settings).select(filter))
    }

    fun deleteOne(id: Int) = transaction {
        dao[id].delete()
    }

    fun delete(filter: ISqlExpressionBuilder.() -> Op<Boolean>) = transaction {
        model.deleteWhere {
            filter(it)
        }
    }

    fun insert(data: List<Create>, body: BatchInsertStatement.(Create) -> Unit) = transaction {
        model.batchInsert(data) {
            body(this, it)
        }.map {
            dao.wrapRow(it).toOutputDto() as Output
        }
    }

    fun insert(data: Create, body: BatchInsertStatement.(Create) -> Unit) = insert(listOf(data), body).first()
}