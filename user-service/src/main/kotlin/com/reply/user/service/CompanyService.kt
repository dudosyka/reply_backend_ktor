package com.reply.user.service

import com.reply.libs.database.dao.CompanyDao
import com.reply.libs.database.dao.FileDao
import com.reply.libs.dto.client.company.CompanyCreateDto
import com.reply.libs.consul.FileServiceClient
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class CompanyService(override val di: DI) : DIAware {
    private val fileServiceClient: FileServiceClient by instance()
    suspend fun create(companyCreateDto: CompanyCreateDto, call: ApplicationCall) = newSuspendedTransaction {
        val companyLogo = fileServiceClient.uploadFile(call, companyCreateDto.logo)
        commit()
        try {
            CompanyDao.new {
                name = companyCreateDto.name
                logo = FileDao[companyLogo.id]
            }
        } catch (e: Exception) {
            fileServiceClient.rollbackUploading(call, companyLogo.id)
            throw e
        }
    }
}