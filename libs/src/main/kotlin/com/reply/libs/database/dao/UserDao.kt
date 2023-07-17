package com.reply.libs.database.dao

import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.database.models.UserModel
import com.reply.libs.dto.client.user.UserOutputDto
import com.reply.libs.utils.database.idValue
import org.jetbrains.exposed.dao.id.EntityID

class UserDao(id: EntityID<Int>): BaseIntEntity<UserOutputDto>(id, UserModel) {
    companion object : BaseIntEntityClass<UserOutputDto, UserDao>(UserModel)

    var login by UserModel.login
    var avatar by FileDao optionalReferencedOn UserModel.avatar
    var hash by UserModel.hash
    var fullname by UserModel.fullname
    var phone by UserModel.phone
    var emailCode by UserModel.emailCode
    var email by UserModel.email
    var role by RoleDao referencedOn UserModel.role
    var coins by UserModel.coins
    var company by CompanyDao referencedOn UserModel.company

    override fun toOutputDto(): UserOutputDto = UserOutputDto(
        idValue, login, avatar?.toOutputDto(), hash, fullname, phone, emailCode, email, role.toOutputDto(), coins, company.toOutputDto()
    )
}