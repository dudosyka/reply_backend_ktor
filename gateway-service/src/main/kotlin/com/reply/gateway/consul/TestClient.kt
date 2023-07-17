package com.reply.gateway.consul

import com.reply.libs.utils.consul.ConsulClient
import org.kodein.di.DI

class TestClient(override val di: DI) : ConsulClient("test")