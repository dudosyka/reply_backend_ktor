package com.reply.test.service

import com.reply.libs.database.dao.*
import com.reply.libs.database.models.CompanyModel
import com.reply.libs.database.models.MetricModel
import com.reply.libs.database.models.QuestionTypeModel
import com.reply.libs.database.models.TestModel
import com.reply.libs.dto.client.test.TestCheckPermissionsDto
import com.reply.libs.dto.client.test.TestCreateDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.dto.internal.AuthorizedUser
import com.reply.libs.dto.internal.exceptions.BadRequestException
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.dto.internal.exceptions.InternalServerError
import com.reply.libs.dto.internal.exceptions.ModelNotFound
import com.reply.libs.utils.crud.CrudService
import com.reply.libs.utils.crud.asDto
import com.reply.libs.utils.database.idValue
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.or
import org.kodein.di.DI
import org.kodein.di.instance

class TestService(di: DI) : CrudService<TestOutputDto, TestCreateDto, TestDao>(di, TestModel, TestDao.Companion) {
    private val questionService: QuestionService by instance()

    suspend fun create(createDto: TestCreateDto, authorizedUser: AuthorizedUser): TestOutputDto = transaction {
        val test = TestDao.new {
            title = createDto.title
            type = QuestionTypeDao[createDto.type]
            company = CompanyDao[authorizedUser.companyId]
            author = UserDao[authorizedUser.id]
            formula = createDto.formula
            metric = MetricDao[createDto.metric]
        }
        questionService.createBatch(test.idValue, createDto.questions)

        commit()
        test
    }.toOutputDto()

    suspend fun getAll(authorizedUser: AuthorizedUser): List<TestOutputDto> = transaction {
        getAllWith(
            {
                (TestModel.company eq authorizedUser.companyId) or
                        (TestModel.company eq null)
            },
            {
                leftJoin(CompanyModel)
                leftJoin(QuestionTypeModel)
                leftJoin(MetricModel)
            }
        ).asDto()
    }

    suspend fun getById(authorizedUser: AuthorizedUser, testId: Int): TestOutputDto = transaction {
        val test = TestDao.findById(testId) ?: throw ModelNotFound()

        //If it is not one of the built-in tests and it is not from requester company restrict access
        if (test.companyId != null && test.companyId != authorizedUser.companyId)
            throw ForbiddenException()

        test.asDto()
    }

    suspend fun getAllByCompany(companyId : Int) : List<TestOutputDto> = transaction{
        getAll{
            TestModel.company eq companyId
        }.asDto()
    }

    suspend fun getByBlock(blockId: Int): List<TestOutputDto> = transaction {
        BlockDao.findById(blockId)?.tests?.asDto() ?: throw BadRequestException()
    }

    suspend fun delete(testId: Int, authorizedUser: AuthorizedUser) = transaction {
        val test = TestDao.findById(testId) ?: throw ModelNotFound("Test with id = $testId not found!")

        //If author is null test is 'built-in' and it is protected from removing
        if (test.author == null) throw ForbiddenException()

        //If company is null or if user is not from company which is the test parent we should restrict access
        if ((test.company?.idValue ?: 0) != authorizedUser.companyId) throw ForbiddenException()

        deleteOne(testId)

        commit()
    }

    suspend fun patch(updateDto: TestCreateDto, testId: Int, authorizedUser: AuthorizedUser): TestDao = transaction {
        val test = TestDao.findById(testId) ?: throw ModelNotFound("Test with id = $testId not found!")

        //If author is null test is 'built-in' and it is protected from removing
        if (test.author == null) throw ForbiddenException()

        //If company is null or if user is not from company which is the test parent we should restrict access
        if ((test.company?.idValue ?: 0) != authorizedUser.companyId) throw ForbiddenException()


        test.apply {
            title = updateDto.title
            type = QuestionTypeDao[updateDto.type]
            company = CompanyDao[authorizedUser.companyId]
            author = UserDao[authorizedUser.id]
            formula = updateDto.formula
            metric = MetricDao[updateDto.metric]
        }

        questionService.updateForTest(testId, updateDto.questions)

        val result = test.flush()

        commit()

        if (!result)
            throw InternalServerError("Test updating failed")

        test
    }

    suspend fun checkPermissions(authorizedUser: AuthorizedUser, permissionsDto: TestCheckPermissionsDto) {
        transaction {
            TestDao.find {
                (TestModel.company eq authorizedUser.companyId) and
                        (TestModel.id inList permissionsDto.tests)

            }.apply { if (count() != permissionsDto.tests.size.toLong()) throw ForbiddenException(
                "Some of these tests are not available to the user"
            ) }
        }
    }
}