package com.reply.libs.database.dao

import com.reply.libs.utils.database.BaseIntEntity
import com.reply.libs.utils.database.BaseIntEntityClass
import com.reply.libs.database.models.UserModel
import org.jetbrains.exposed.dao.id.EntityID

class UserDao(id: EntityID<Int>): BaseIntEntity(id, UserModel) {
    companion object : BaseIntEntityClass<UserDao>(UserModel)

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
}