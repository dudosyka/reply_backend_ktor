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
import com.reply.libs.dto.internal.exceptions.ModelNotFound
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.crud.asDto
import com.reply.libs.utils.database.idValue
import io.ktor.server.application.*
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.instance

class BlockService(di : DI) : CrudService<BlockOutputDto, BlockCreateDto, BlockDao>(di, BlockModel, BlockDao.Companion) {
    private val testClient : TestClient by instance()
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

    fun getAll(authorizedUser: AuthorizedUser) = getAll { BlockModel.company eq authorizedUser.companyId }.asDto()

    suspend fun patch(updateDto: BlockCreateDto, blockId: Int, authorizedUser : AuthorizedUser, call: ApplicationCall) = newSuspendedTransaction{
        val block = BlockDao.findById(blockId)?: throw ModelNotFound("Block with id = $blockId not found!")


        //Checking for the right to change the block
        if (authorizedUser.companyId != block.company.idValue) throw ForbiddenException()

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


    fun delete(blockId: Int, authorizedUser : AuthorizedUser) = transaction{
        getOne(blockId, authorizedUser).delete()

        SuccessOutputDto("success", "Group successfully deleted")
    }

    private fun  getOne(blockId: Int, authorizedUser: AuthorizedUser) = transaction {
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
            if (company.idValue == authorizedUser.companyId) throw ForbiddenException()}.asDto().apply {
                tests = testClient.withCall(call) {
                get<List<TestOutputDto>>("test/block/$blockId")!!
            }
        }
    }

//   fun getToken(userId : Int, week : Int, blockId: Int, call: ApplicationCall) : AuthOutputDto {
//        AuthOutputDto(
//            createToken(
//                mutableMapOf(
//                    "userId" to userId.toString(),
//                    "role" to
//                    "login" to
//                    "blockId" to blockId.toString(),
//                    "companyId" to
//                    "week" to  week.toString()
//                )
//            )
//        )
//    }
}