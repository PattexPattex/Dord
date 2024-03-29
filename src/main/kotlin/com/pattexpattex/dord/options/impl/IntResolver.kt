package com.pattexpattex.dord.options.impl

import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.types.ChoiceMapper
import com.pattexpattex.dord.options.types.ComponentOptionResolver
import com.pattexpattex.dord.options.types.SlashOptionResolver
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.typeOf

class IntResolver :
    OptionResolver<IntResolver, Int>(typeOf<Int>()),
    SlashOptionResolver<IntResolver, Int>,
    ComponentOptionResolver<IntResolver, Int>,
    ChoiceMapper<IntResolver, Int> {
    override val optionType = OptionType.INTEGER

    override suspend fun toChoice(value: Int) = Command.Choice(value.toString(), value.toLong())

    override suspend fun resolve(
        handlerName: String,
        event: GenericComponentInteractionCreateEvent,
        arg: String,
    ) = StringResolver().resolve(handlerName, event, arg)?.toIntOrNull()

    override suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?) = optionMapping?.asInt
}