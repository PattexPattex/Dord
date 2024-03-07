package com.pattexpattex.dord.options.impl

import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.types.ChoiceMapper
import com.pattexpattex.dord.options.types.ComponentOptionResolver
import com.pattexpattex.dord.options.types.ModalOptionResolver
import com.pattexpattex.dord.options.types.SlashOptionResolver
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.modals.ModalMapping
import kotlin.reflect.typeOf

class StringResolver :
    OptionResolver<StringResolver, String>(typeOf<String>()),
    SlashOptionResolver<StringResolver, String>,
    ComponentOptionResolver<StringResolver, String>,
    ModalOptionResolver<StringResolver, String>,
    ChoiceMapper<StringResolver, String> {
    override val optionType = OptionType.STRING

    override suspend fun toChoice(value: String) = Command.Choice(value, value)

    override suspend fun resolve(
        handlerName: String,
        event: GenericComponentInteractionCreateEvent,
        arg: String,
    ): String? {
        val (regex, groups) = ComponentOptionResolver.handlerPatternToRegexWithGroups(handlerName)
        if (arg !in groups) return null

        return regex.matchEntire(event.componentId)!!.groups[arg]?.value
    }

    override suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?) =
        optionMapping?.asString

    override suspend fun resolve(event: ModalInteractionEvent, mapping: ModalMapping?) = mapping?.asString
}