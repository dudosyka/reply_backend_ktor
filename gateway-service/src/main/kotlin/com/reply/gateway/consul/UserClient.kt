package com.reply.gateway.consul

import com.reply.libs.utils.consul.ConsulClient
import org.kodein.di.DI

class UserClient(override val di: DI) : ConsulClient("user")