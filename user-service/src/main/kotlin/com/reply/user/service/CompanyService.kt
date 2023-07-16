package com.reply.user.service

import com.reply.libs.database.dao.CompanyDao
import com.reply.libs.database.dao.FileDao
import com.reply.libs.dto.client.company.CreateCompanyDto
import com.reply.libs.dto.client.file.CreateFileDto
import com.reply.libs.dto.client.file.FileDto
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.utils.consul.EmptyBody
import com.reply.user.consul.FileServiceClient
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class CompanyService(override val di: DI) : DIAware {
    private val fileServiceClient: FileServiceClient by instance()
    suspend fun create(createCompanyDto: CreateCompanyDto, call: ApplicationCall) = newSuspendedTransaction {
        val companyLogo = fileServiceClient.withCall(call) {
            internal {
                post<CreateFileDto, FileDto>("upload", input = createCompanyDto.logo)!!
            }
        }
        commit()
        try {
            CompanyDao.new {
                name = createCompanyDto.name
                logo = FileDao[companyLogo.id]
            }
        } catch (e: Exception) {
            fileServiceClient.withCall(call) {
                internal {
                    noExceptionBubble {
                        delete<EmptyBody, SuccessOutputDto>("rollback/${companyLogo.id}", input = EmptyBody)
                    }
                }
            }
            throw e
        }
    }
}