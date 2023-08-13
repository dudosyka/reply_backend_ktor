package com.reply.libs.utils.consul

import java.net.ServerSocket

object FreePortGenerator {
    var port: Int? = null
    operator fun invoke(): Int {
        if (port != null)
            return port!!
        val socket = ServerSocket(0)
        port = socket.localPort
        socket.close()
        return port!!
    }
}