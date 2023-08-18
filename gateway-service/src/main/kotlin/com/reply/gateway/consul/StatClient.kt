package com.reply.gateway.consul

import com.reply.libs.config.ApiConfig
import com.reply.libs.utils.consul.ConsulClient
import org.kodein.di.DI

class StatClient(override val di: DI) : ConsulClient(ApiConfig.statServiceName)