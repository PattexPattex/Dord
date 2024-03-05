package com.pattexpattex.dord.options.impl

import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.types.ComponentOptionResolver
import com.pattexpattex.dord.options.types.SlashOptionResolver
import net.dv8tion.jda.api.entities.Guild
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.typeOf

class GuildResolver :
    OptionResolver<GuildResolver, Guild>(typeOf<Guild>()),
    SlashOptionResolver<GuildResolver, Guild>,
    ComponentOptionResolver<GuildResolver, Guild> {
    override val optionType = OptionType.STRING

    override suspend fun resolve(
        handlerName: String,
        event: GenericComponentInteractionCreateEvent,
        arg: String,
    ) = StringResolver().resolve(handlerName, event, arg)?.let(event.jda::getGuildById)

    override suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?) =
        optionMapping?.asString?.let(event.jda::getGuildById)
}