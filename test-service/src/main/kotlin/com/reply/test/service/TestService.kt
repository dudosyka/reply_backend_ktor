package com.reply.test.service

import com.reply.libs.database.dao.*
import com.reply.libs.database.models.*
import com.reply.libs.dto.client.test.TestCreateDto
import com.reply.libs.dto.client.test.TestOutputDto
import com.reply.libs.dto.internal.AuthorizedUser
import com.reply.libs.dto.internal.exceptions.ForbiddenException
import com.reply.libs.dto.internal.exceptions.ModelNotFound
import com.reply.libs.utils.database.idValue
import org.jetbrains.exposed.sql.or
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.kodein.di.DI
import org.kodein.di.DIAware
import org.kodein.di.instance

class TestService(override val di: DI) : DIAware {
    private val questionService: QuestionService by instance()

    fun create(createDto: TestCreateDto, admin: AuthorizedUser): TestOutputDto = transaction {
        val test = TestDao.new {
            title = createDto.title
            type = QuestionTypeDao[createDto.type]
            company = CompanyDao[admin.companyId]
            author = UserDao[admin.id]
            formula = createDto.formula
            metric = MetricDao[createDto.metric]
        }
        questionService.createBatch(test.idValue, createDto.questions)
        test
    }.toOutputDto()

    fun getAll(admin: AuthorizedUser): MutableList<TestOutputDto> = transaction {
        val query =
            TestModel.leftJoin(CompanyModel).leftJoin(QuestionTypeModel).leftJoin(MetricModel)
                .select { (TestModel.company eq admin.companyId) or (TestModel.company eq null) }

        TestDao.wrapRows(query).toMutableList().map {
            it.toOutputDto()
        }.toMutableList()
    }

    fun getOne(testId: Int): TestOutputDto = transaction {
        TestDao.findById(testId)?.toOutputDto() ?: throw ModelNotFound("Test with id = $testId not found!")
    }

    fun delete(testId: Int, admin: AuthorizedUser) = transaction {
        val test = TestDao.findById(testId) ?: throw ModelNotFound("Test with id = $testId not found!")

        //If author is null test is 'built-in' and it is protected from removing
        if (test.author == null) throw ForbiddenException()

        //If company is null or if user is not from company which is the test parent we should restrict access
        if ((test.company?.idValue ?: 0) != admin.companyId) throw ForbiddenException()

        test.delete()
    }

    fun patch(updateDto: TestCreateDto, testId: Int, admin: AuthorizedUser) = transaction {
        val test = TestDao.findById(testId) ?: throw ModelNotFound("Test with id = $testId not found!")

        //If author is null test is 'built-in' and it is protected from removing
        if (test.author == null) throw ForbiddenException()

        //If company is null or if user is not from company which is the test parent we should restrict access
        if ((test.company?.idValue ?: 0) != admin.companyId) throw ForbiddenException()


        test.apply {
            title = updateDto.title
            type = QuestionTypeDao[updateDto.type]
            company = CompanyDao[admin.companyId]
            author = UserDao[admin.id]
            formula = updateDto.formula
            metric = MetricDao[updateDto.metric]
        }

        questionService.updateForTest(testId, updateDto.questions)

        test.flush()
    }

}