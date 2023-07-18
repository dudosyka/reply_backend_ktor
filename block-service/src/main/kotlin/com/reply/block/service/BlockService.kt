package com.reply.block.service

import com.reply.gateway.consul.TestClient
import com.reply.libs.database.dao.BlockDao
import com.reply.libs.database.dao.CompanyDao
import com.reply.libs.database.dao.TestDao
import com.reply.libs.database.models.BlockModel
import com.reply.libs.database.models.TestModel
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.block.BlockCreateDto
import com.reply.libs.dto.client.block.BlockOutputDto
import com.reply.libs.dto.client.test.TestCheckPermissionsDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.dto.internal.AuthorizedUser
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.crud.asDto
import com.reply.libs.utils.database.idValue
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.kodein.di.DI
import org.kodein.di.instance

class BlockService(di : DI) : CrudService<BlockOutputDto, BlockCreateDto, BlockDao>(di, BlockModel, BlockDao.Companion) {
    private val testClient : TestClient by instance()
    suspend fun create(createDto: BlockCreateDto, admin : AuthorizedUser, call: ApplicationCall) : BlockOutputDto = newSuspendedTransaction {
        testClient.withCall(call){
            post<TestCheckPermissionsDto, SuccessOutputDto>("check/permissions", TestCheckPermissionsDto(createDto.tests))
        }
        BlockDao.new {
            name = createDto.name
            description = createDto.description
            time = createDto.time
            company = CompanyDao[admin.companyId]
            tests = TestDao.find { TestModel.id inList createDto.tests }
        }
    }.asDto()

    fun getAll(authorizedUser: AuthorizedUser) = getAll { BlockModel.company eq authorizedUser.companyId }.asDto()

    suspend fun getOne(
        call : ApplicationCall,
        blockId : Int,
        authorizedUser : AuthorizedUser
    ) = newSuspendedTransaction {
        getOne(blockId).apply {
            if (company.idValue == authorizedUser.companyId) throw ForbiddenException()}.asDto().apply {
                tests = testClient.withCall(call) {
                get<List<TestOutputDto>>("test/block/$blockId")!!
            }
        }
    }
}