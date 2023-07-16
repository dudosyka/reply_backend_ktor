package com.reply.user.consul

import com.reply.libs.utils.consul.ConsulClient
import org.kodein.di.DI


class FileServiceClient(override val di: DI) : ConsulClient("file")