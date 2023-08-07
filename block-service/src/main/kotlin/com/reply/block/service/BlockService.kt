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
import com.reply.libs.dto.internal.exceptions.InternalServerError
import com.reply.libs.dto.internal.exceptions.ModelNotFound
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.crud.asDto
import com.reply.libs.utils.database.idValue
import io.ktor.server.application.*
import org.kodein.di.DI
import org.kodein.di.instance

class BlockService(di : DI) : CrudService<BlockOutputDto, BlockCreateDto, BlockDao>(di, BlockModel, BlockDao.Companion) {
    private val testClient : TestClient by instance()
    private val userClient : UserClient by instance()
    suspend fun create(createDto: BlockCreateDto, authorizedUser : AuthorizedUser, call: ApplicationCall) : BlockOutputDto = transaction {
        testClient.withCall(call){
            post<TestCheckPermissionsDto, SuccessOutputDto>("test/check/permissions", TestCheckPermissionsDto(createDto.tests))
        }

        val providedTests = TestDao.find { TestModel.id inList createDto.tests }
        if (providedTests.count() != createDto.tests.size.toLong())
            throw ModelNotFound("Tests not found")

        val dto = BlockDao.new {
            name = createDto.name
            description = createDto.description
            time = createDto.time
            company = CompanyDao[authorizedUser.companyId]
            tests = providedTests
        }.toClientOutput()
        commit()
        dto
    }

    suspend fun getAll(authorizedUser: AuthorizedUser) = transaction {
        getAll { BlockModel.company eq authorizedUser.companyId }.asDto()
    }

    suspend fun getAllByCompany(companyId : Int) = transaction{
        getAll {BlockModel.company eq companyId}.asDto()
    }
    suspend fun getOne(
        call : ApplicationCall,
        blockId : Int,
        authorizedUser : AuthorizedUser
    ) = transaction {
        getOne(blockId).apply {
            if (companyId != authorizedUser.companyId) throw ForbiddenException()
        }.asDto().apply {
            tests = testClient.withCall(call) {
                get<List<TestOutputDto>>("test/block/$blockId")!!
            }
        }
    }

    suspend fun patch(updateDto: BlockCreateDto, blockId: Int, authorizedUser : AuthorizedUser, call: ApplicationCall): BlockDao = transaction{
        val block = getById(blockId)

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

        val flush = block.flush()

        commit()

        if (!flush)
            throw InternalServerError("Block updating failed")

        block
    }


    suspend fun delete(blockId: Int, authorizedUser: AuthorizedUser): SuccessOutputDto = transaction{
        val block = getById(blockId)

        if (block.companyId != authorizedUser.companyId) throw ForbiddenException()

        block.delete()

        commit()
        SuccessOutputDto("success", "Block successfully removed")
    }

    suspend fun getToken(userId : Int, week : Int, blockId: Int, call: ApplicationCall) : AuthOutputDto = transaction {
        val block = getById(blockId)

        userClient.withCall(call){
            post<BlockTokenDto, AuthOutputDto>("token/block",
                BlockTokenDto(
                    blockId =  block.idValue,
                    week = week,
                    userId = userId
                )
            )!!
        }
   }
    private suspend fun getById(blockId: Int) : BlockDao = transaction {
        BlockDao.findById(blockId)?: throw ModelNotFound("Block with id = $blockId not found!")
    }

    private fun checkRightToBlock(authorizedUser: AuthorizedUser, block: BlockDao){
        if (authorizedUser.companyId != block.companyId) throw ForbiddenException()
    }
}