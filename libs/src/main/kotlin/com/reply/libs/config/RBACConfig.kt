package com.reply.libs.config

enum class RBACConfig(val stringRole: String, val roleId: Int) {
    ADMIN("ADMIN", 1) {
        override fun toString(): String {
            return stringRole
        }
    },
    CLIENT("CLIENT", 2) {
        override fun toString(): String {
            return stringRole
        }
    },
    AUTHORIZED("AUTHORIZED", 0) {
        override fun toString(): String {
            return stringRole
        }
    }
}