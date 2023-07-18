package com.reply.libs.database.models

import org.jetbrains.exposed.sql.Table

object GroupUsersModel : Table() {
    val user = reference("user", UserModel)
    val group = reference("group", GroupModel)
    override val primaryKey = PrimaryKey(user, group)
}