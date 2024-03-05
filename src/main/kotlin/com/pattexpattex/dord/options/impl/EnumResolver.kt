package com.pattexpattex.dord.options.impl

import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.types.ComponentOptionResolver
import com.pattexpattex.dord.options.types.SlashOptionResolver
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.KType

class EnumResolver<E : Enum<E>>(private val type: KType, private val enumValues: Collection<E>) :
    OptionResolver<EnumResolver<E>, E>(type),
    SlashOptionResolver<EnumResolver<E>, E>,
    ComponentOptionResolver<EnumResolver<E>, E> {
    override val optionType = OptionType.STRING

    override suspend fun resolve(
        handlerName: String,
        event: GenericComponentInteractionCreateEvent,
        arg: String,
    ) = StringResolver().resolve(handlerName, event, arg)?.let(::getEnumValue)

    override suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?) =
        optionMapping?.asString?.let(::getEnumValue)

    private val map = enumValues.associateBy { it.name.lowercase() }

    private fun getEnumValue(name: String) = map[name]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as EnumResolver<*>

        if (type != other.type) return false
        if (enumValues != other.enumValues) return false

        return true
    }

    override fun hashCode(): Int {
        var result = type.hashCode()
        result = 31 * result + enumValues.hashCode()
        return result
    }
}
