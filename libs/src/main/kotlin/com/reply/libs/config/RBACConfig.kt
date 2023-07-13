package com.reply.libs.config

enum class RBACConfig(val stringRole: String) {
    ADMIN("ADMIN") {
        override fun toString(): String {
            return stringRole
        }
    },
    CLIENT("CLIENT") {
        override fun toString(): String {
            return stringRole
        }
    },
    AUTHORIZED("AUTHORIZED") {
        override fun toString(): String {
            return stringRole
        }
    }
}