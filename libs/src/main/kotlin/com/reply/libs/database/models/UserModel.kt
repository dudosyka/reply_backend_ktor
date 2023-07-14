package com.reply.libs.database.models

import com.reply.libs.config.database.BaseIntIdTable

object UserModel: BaseIntIdTable() {
    val login = varchar("login", 1024).uniqueIndex("unique_login_index")
    val avatar = reference("avatar", FileModel).nullable().default(null)
    val hash = varchar("hash", 1024)
    val emailCode = integer("emailCode").nullable().default(null)
    val email = varchar("email", 1024).uniqueIndex("unique_email_index")
    val role = reference("role", RoleModel)
    val coins = integer("coins").default(0)
}