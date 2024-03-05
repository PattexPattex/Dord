package com.pattexpattex.dord.options.types

import com.pattexpattex.dord.options.OptionResolver
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType

interface SlashOptionResolver<T, R : Any> where T : OptionResolver<T, R>, T : SlashOptionResolver<T, R> {
    val optionType: OptionType
    suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?): R?

    companion object
}