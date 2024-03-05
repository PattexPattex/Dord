package com.pattexpattex.dord.options.impl

import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.types.ComponentOptionResolver
import com.pattexpattex.dord.options.types.SlashOptionResolver
import com.pattexpattex.dord.options.types.UserContextOptionResolver
import dev.minn.jda.ktx.coroutines.await
import net.dv8tion.jda.api.entities.User
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.typeOf

class UserResolver :
    OptionResolver<UserResolver, User>(typeOf<User>()),
    SlashOptionResolver<UserResolver, User>,
    ComponentOptionResolver<UserResolver, User>,
    UserContextOptionResolver<UserResolver, User> {
    override val optionType = OptionType.USER

    override suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?) =
        optionMapping?.asUser

    override suspend fun resolve(
        handlerName: String,
        event: GenericComponentInteractionCreateEvent,
        arg: String
    ) = StringResolver().resolve(handlerName, event, arg)?.let { event.jda.retrieveUserById(it) }?.await()

    override suspend fun resolve(event: UserContextInteractionEvent) = event.target
}