package com.pattexpattex.dord

import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.entities.User

internal class InputUserImpl internal constructor(
    user: User,
    override val member: Member?
) : InputUser, User by user {
    constructor(member: Member) : this(member.user, member)
    constructor(user: User) : this(user, null)
}

interface InputUser : User {
    val member: Member?
}