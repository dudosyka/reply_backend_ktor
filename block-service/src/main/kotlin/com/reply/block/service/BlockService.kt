package com.reply.block.service

import com.reply.gateway.consul.TestClient
import com.reply.gateway.consul.UserClient
import com.reply.libs.database.dao.BlockDao
import com.reply.libs.database.dao.CompanyDao
import com.reply.libs.database.dao.TestDao
import com.reply.libs.database.models.BlockModel
import com.reply.libs.database.models.TestModel
import com.reply.libs.dto.client.auth.AuthOutputDto
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.block.BlockCreateDto
import com.reply.libs.dto.client.block.BlockOutputDto
import com.reply.libs.dto.client.block.BlockTokenDto
import com.reply.libs.dto.client.test.TestCheckPermissionsDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.dto.internal.AuthorizedUser
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.dto.internal.exceptions.ModelNotFound
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.crud.asDto
import com.reply.libs.utils.database.idValue
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.kodein.di.DI
import org.kodein.di.instance

class BlockService(di : DI) : CrudService<BlockOutputDto, BlockCreateDto, BlockDao>(di, BlockModel, BlockDao.Companion) {
    private val testClient : TestClient by instance()
    private val userClient : UserClient by instance()
    suspend fun create(createDto: BlockCreateDto, authorizedUser : AuthorizedUser, call: ApplicationCall) : BlockOutputDto = newSuspendedTransaction {
        testClient.withCall(call){
            post<TestCheckPermissionsDto, SuccessOutputDto>("test/check/permissions", TestCheckPermissionsDto(createDto.tests))
        }
        BlockDao.new {
            name = createDto.name
            description = createDto.description
            time = createDto.time
            company = CompanyDao[authorizedUser.companyId]
            tests = TestDao.find { TestModel.id inList createDto.tests }
        }.toClientOutput()

    }

    suspend fun getAll(authorizedUser: AuthorizedUser) = transaction {
        getAll { BlockModel.company eq authorizedUser.companyId }.asDto()
    }

    suspend fun getAllByCompany(companyId : Int) = transaction{
        getAll {BlockModel.company eq companyId}.asDto()
    }

    suspend fun patch(updateDto: BlockCreateDto, blockId: Int, authorizedUser : AuthorizedUser, call: ApplicationCall) = newSuspendedTransaction{
        //Checking for the existence of a block
        val block = checkBlock(blockId)

        //Checking for the right to change the block
        checkRightToBlock(authorizedUser, block)

        //Checking for admission to the specified tests
        testClient.withCall(call){
            post<TestCheckPermissionsDto, SuccessOutputDto>("test/check/permissions", TestCheckPermissionsDto(updateDto.tests))
        }

        block.apply {
            name = updateDto.name
            description = updateDto.description
            time = updateDto.time
            tests = TestDao.find { TestModel.id inList updateDto.tests }
        }

        block.flush()
    }


    suspend fun delete(blockId: Int, authorizedUser : AuthorizedUser): SuccessOutputDto = transaction{
        getOne(blockId, authorizedUser).delete()

        SuccessOutputDto("success", "Block successfully deleted")
    }

    private suspend fun  getOne(blockId: Int, authorizedUser: AuthorizedUser) = transaction {
        val block = getOne(blockId)

        if (block.company.idValue != authorizedUser.companyId) throw ForbiddenException()

        block
    }

    suspend fun getOne(
        call : ApplicationCall,
        blockId : Int,
        authorizedUser : AuthorizedUser
    ) = newSuspendedTransaction {
        getOne(blockId).apply {
            if (company.idValue != authorizedUser.companyId) throw ForbiddenException()}.asDto().apply {
                tests = testClient.withCall(call) {
                get<List<TestOutputDto>>("test/block/$blockId")!!
            }
        }
    }
   suspend fun getToken(userId : Int, week : Int, blockId: Int, call: ApplicationCall) : AuthOutputDto = newSuspendedTransaction {
       //Checking for the existence of a block
       checkBlock(blockId)


       userClient.withCall(call){
            post<BlockTokenDto, AuthOutputDto>("token/block",
                BlockTokenDto(
                    blockId =  blockId,
                    week = week,
                    userId = userId
                )
            )!!
       }
   }
    private fun checkBlock(blockId: Int) : BlockDao{
        return BlockDao.findById(blockId)?: throw ModelNotFound("Block with id = $blockId not found!")
    }

    private fun checkRightToBlock(authorizedUser: AuthorizedUser, block: BlockDao){
        if (authorizedUser.companyId != block.company.idValue) throw ForbiddenException()
    }

}