package com.reply.libs.utils.crud

import com.reply.libs.dto.internal.exceptions.ModelNotFound
import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.utils.database.BaseIntIdTable
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.DIAware

@Suppress("UNCHECKED_CAST")
open class CrudService<Output, Create, Dao>(override val di: DI, private val model: BaseIntIdTable, val dao: BaseIntEntityClass<Output, *>) : DIAware {

    open fun getOne(id: Int): Dao = transaction {
        dao[id] as Dao
    }

    fun getOneWith(id: Int, settings: BaseIntIdTable.() -> Unit): Dao = transaction {
        val result = getAllWith({
            model.id eq id
        }, settings)

        if (result.isEmpty())
            throw ModelNotFound()

        result.first()
    }

    fun getAll(filter: SqlExpressionBuilder.() -> Op<Boolean> = { Op.nullOp() }): List<Dao> = transaction {
        dao.wrapRows(model.select(filter)).map { it as Dao }
    }

    fun getAllWith(filter: SqlExpressionBuilder.() -> Op<Boolean> = { Op.nullOp() }, settings: BaseIntIdTable.() -> Unit): List<Dao> = transaction {
        dao.wrapRows(model.apply(settings).select(filter)).map { it as Dao }
    }

    fun deleteOne(id: Int) = transaction {
        dao[id].delete()
    }

    fun delete(filter: ISqlExpressionBuilder.() -> Op<Boolean>) = transaction {
        model.deleteWhere {
            filter(it)
        }
    }

    fun insert(data: List<Create>, body: BatchInsertStatement.(Create) -> Unit): List<Dao> = transaction {
        model.batchInsert(data) {
            body(this, it)
        }.map {
            dao.wrapRow(it) as Dao
        }
    }

    fun insert(data: Create, body: BatchInsertStatement.(Create) -> Unit) = insert(listOf(data), body).first()
}

fun <Entity> List<BaseIntEntity<Entity>>.asDto() = this.map { it.toOutputDto() }
fun <Entity> SizedIterable<BaseIntEntity<Entity>>.asDto() = this.map { it.toOutputDto() }
fun <Entity> BaseIntEntity<Entity>.asDto() = this.toOutputDto()