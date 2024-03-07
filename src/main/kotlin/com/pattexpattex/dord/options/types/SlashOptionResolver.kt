package com.pattexpattex.dord.options.types

import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.Resolvers
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

interface SlashOptionResolver<T, R : Any> where T : OptionResolver<T, R>, T : SlashOptionResolver<T, R> {
    val optionType: OptionType
    suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?): R?

    companion object {
        inline fun <reified T> toOptionType() = Resolvers
            .resolvers
            .filter { typeOf<T>().withNullability(false).isSubtypeOf(it.resolvedType) }
            .filterIsInstance<SlashOptionResolver<*, *>>()
            .firstOrNull()
            ?.optionType
    }
}