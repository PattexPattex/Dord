package com.pattexpattex.dord.options.impl

import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.types.ComponentOptionResolver
import com.pattexpattex.dord.options.types.SlashOptionResolver
import com.pattexpattex.dord.options.types.UserContextOptionResolver
import dev.minn.jda.ktx.coroutines.await
import net.dv8tion.jda.api.entities.Member
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.typeOf

class MemberResolver :
    OptionResolver<MemberResolver, Member>(typeOf<Member>()),
    SlashOptionResolver<MemberResolver, Member>,
    ComponentOptionResolver<MemberResolver, Member>,
    UserContextOptionResolver<MemberResolver, Member> {
    override val optionType = OptionType.USER

    override suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?) =
        optionMapping?.asMember

    override suspend fun resolve(
        handlerName: String,
        event: GenericComponentInteractionCreateEvent,
        arg: String,
    ) = StringResolver().resolve(handlerName, event, arg)?.let { event.guild?.retrieveMemberById(it) }?.await()

    override suspend fun resolve(event: UserContextInteractionEvent) = event.targetMember
}