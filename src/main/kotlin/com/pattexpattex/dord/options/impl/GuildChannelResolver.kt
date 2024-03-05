package com.pattexpattex.dord.options.impl

import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.types.ComponentOptionResolver
import com.pattexpattex.dord.options.types.SlashOptionResolver
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.typeOf

class GuildChannelResolver :
    OptionResolver<GuildChannelResolver, GuildChannel>(typeOf<GuildChannel>()),
    SlashOptionResolver<GuildChannelResolver, GuildChannel>,
    ComponentOptionResolver<GuildChannelResolver, GuildChannel> {
    override val optionType = OptionType.CHANNEL

    override suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?) =
        optionMapping?.asChannel

    override suspend fun resolve(
        handlerName: String,
        event: GenericComponentInteractionCreateEvent,
        arg: String,
    ) = LongResolver().resolve(handlerName, event, arg)?.let(event.jda::getGuildChannelById)
}