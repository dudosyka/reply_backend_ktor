package com.reply.libs.utils.bcrypt

import org.mindrot.jbcrypt.BCrypt

object PasswordUtil {
    fun hash(password: String) = BCrypt.hashpw(password, BCrypt.gensalt())

    fun compare(password: String, hash: String) = BCrypt.checkpw(password, hash)
}