package com.pattexpattex.dord.options.impl

import com.pattexpattex.dord.InputUser
import com.pattexpattex.dord.InputUserImpl
import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.types.ComponentOptionResolver
import com.pattexpattex.dord.options.types.SlashOptionResolver
import com.pattexpattex.dord.options.types.UserContextOptionResolver
import dev.minn.jda.ktx.coroutines.await
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.typeOf

class InputUserResolver :
    OptionResolver<InputUserResolver, InputUser>(typeOf<InputUser>()),
    SlashOptionResolver<InputUserResolver, InputUser>,
    ComponentOptionResolver<InputUserResolver, InputUser>,
    UserContextOptionResolver<InputUserResolver, InputUser> {
    override val optionType = OptionType.USER

    override suspend fun resolve(
        handlerName: String,
        event: GenericComponentInteractionCreateEvent,
        arg: String,
    ): InputUser? = coroutineScope {
        val id = StringResolver().resolve(handlerName, event, arg) ?: return@coroutineScope null

        val user = async { event.jda.retrieveUserById(id).await() }
        val member = async { event.guild?.retrieveMemberById(id)?.await() }

        InputUserImpl(user.await(), member.await())
    }

    override suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?): InputUser? =
        optionMapping?.let { InputUserImpl(it.asUser, it.asMember) }

    override suspend fun resolve(event: UserContextInteractionEvent): InputUser =
        InputUserImpl(event.target, event.targetMember)
}