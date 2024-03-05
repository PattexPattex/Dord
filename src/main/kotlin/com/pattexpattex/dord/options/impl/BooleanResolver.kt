package com.pattexpattex.dord.options.impl

import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.types.ComponentOptionResolver
import com.pattexpattex.dord.options.types.SlashOptionResolver
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.typeOf

class BooleanResolver :
    OptionResolver<BooleanResolver, Boolean>(typeOf<Boolean>()),
    SlashOptionResolver<BooleanResolver, Boolean>,
    ComponentOptionResolver<BooleanResolver, Boolean> {
    override val optionType = OptionType.BOOLEAN

    override suspend fun resolve(
        handlerName: String,
        event: GenericComponentInteractionCreateEvent,
        arg: String,
    ) = StringResolver().resolve(handlerName, event, arg)?.toBooleanStrictOrNull()

    override suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?) =
        optionMapping?.asBoolean
}