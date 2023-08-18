package com.reply.test.service

import com.reply.libs.database.dao.*
import com.reply.libs.database.models.BlockModel
import com.reply.libs.database.models.QuestionModel
import com.reply.libs.database.models.TestModel
import com.reply.libs.dto.client.auth.AuthOutputDto
import com.reply.libs.dto.client.base.SuccessOutputDto
import com.reply.libs.dto.client.block.BlockCreateDto
import com.reply.libs.dto.client.block.BlockOnPassDto
import com.reply.libs.dto.client.block.BlockOutputDto
import com.reply.libs.dto.client.block.BlockTokenDto
import com.reply.libs.dto.client.question.QuestionOnPassDto
import com.reply.libs.dto.client.test.TestCheckPermissionsDto
import com.reply.libs.dto.client.test.TestOnPassDto
import com.reply.libs.dto.internal.AuthorizedUser
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.dto.internal.exceptions.InternalServerError
import com.reply.libs.dto.internal.exceptions.ModelNotFound
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.crud.asDto
import com.reply.libs.utils.database.idValue
import com.reply.test.consul.UserClient
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import org.kodein.di.DI
import org.kodein.di.instance

class BlockService(di : DI) : CrudService<BlockOutputDto, BlockCreateDto, BlockDao>(di, BlockModel, BlockDao.Companion) {
    private val userClient : UserClient by instance()
    private val testService: TestService by instance()
    suspend fun create(createDto: BlockCreateDto, authorizedUser : AuthorizedUser) : BlockOutputDto = transaction {
        testService.checkPermissions(authorizedUser, TestCheckPermissionsDto(createDto.tests))

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
        blockId : Int,
        authorizedUser : AuthorizedUser
    ) = transaction {
        getOne(blockId).apply {
            if (companyId != authorizedUser.companyId) throw ForbiddenException()
        }.asDto().apply {
            tests = testService.getByBlock(blockId)
        }
    }

    suspend fun patch(updateDto: BlockCreateDto, blockId: Int, authorizedUser : AuthorizedUser): BlockDao = transaction{
        val block = getById(blockId)

        //Checking for the right to change the block
        checkPermissions(authorizedUser, block)

        //Checking for admission to the specified tests
        testService.checkPermissions(authorizedUser, TestCheckPermissionsDto(updateDto.tests))

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

    suspend fun getToken(blockTokenDto: BlockTokenDto, call: ApplicationCall, authorizedUser: AuthorizedUser) : AuthOutputDto = transaction {
        val block = getById(blockTokenDto.blockId)
        val user = UserDao.findById(blockTokenDto.userId) ?: throw ModelNotFound("User with id = ${blockTokenDto.userId}")

        if (user.companyId != authorizedUser.companyId)
            throw ForbiddenException()

        if (authorizedUser.companyId != block.companyId)
            throw ForbiddenException()

        userClient.withCall(call){
            post<BlockTokenDto, AuthOutputDto>("token/block",
                BlockTokenDto(
                    blockId =  block.idValue,
                    week = blockTokenDto.week,
                    userId = blockTokenDto.userId
                )
            )!!
        }
   }
    private suspend fun getById(blockId: Int) : BlockDao = transaction {
        BlockDao.findById(blockId)?: throw ModelNotFound("Block with id = $blockId not found!")
    }

    private fun checkPermissions(authorizedUser: AuthorizedUser, block: BlockDao){
        if (authorizedUser.companyId != block.companyId) throw ForbiddenException()
    }

    suspend fun getOnPass(authorized: AuthorizedUser): BlockOnPassDto = transaction {
        val blockId = authorized.blockId ?: throw ForbiddenException()
        val block = BlockDao.findById(blockId) ?: throw ModelNotFound()

        BlockOnPassDto(
            block.idValue,
            block.createdAt.toString(),
            block.updatedAt.toString(),
            block.name,
            block.description,
            block.tests.map {
                test -> run {
                    TestOnPassDto(
                        test.idValue,
                        test.title,
                        test.createdAt.toString(),
                        test.updatedAt.toString(),
                        test.metricId,
                        QuestionDao.find { QuestionModel.test eq test.idValue }.map {
                            question -> run {
                                QuestionOnPassDto(
                                    question.idValue,
                                    question.title,
                                    question.typeId.value,
                                    question.relative_id,
                                    Json.decodeFromString(question.value),
                                    question.coins,
                                    question.pictureId?.value
                                )
                            }
                        }
                    )
                }
            }
        )
    }
}