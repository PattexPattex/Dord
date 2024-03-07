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

class DoubleResolver :
    OptionResolver<DoubleResolver, Double>(typeOf<Double>()),
    SlashOptionResolver<DoubleResolver, Double>,
    ComponentOptionResolver<DoubleResolver, Double>,
    ChoiceMapper<DoubleResolver, Double> {
    override val optionType = OptionType.NUMBER

    override suspend fun toChoice(value: Double) = Command.Choice(value.toString(), value)

    override suspend fun resolve(
        handlerName: String,
        event: GenericComponentInteractionCreateEvent,
        arg: String,
    ) = StringResolver().resolve(handlerName, event, arg)?.toDoubleOrNull()

    override suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?) =
        optionMapping?.asDouble
}