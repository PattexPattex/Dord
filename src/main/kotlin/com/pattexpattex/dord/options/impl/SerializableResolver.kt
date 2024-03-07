package com.pattexpattex.dord.options.impl

import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.types.ChoiceMapper
import com.pattexpattex.dord.options.types.ComponentOptionResolver
import com.pattexpattex.dord.options.types.SlashOptionResolver
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.KType

class SerializableResolver<T : Any>(private val serializer: KSerializer<T>, private val type: KType) :
    OptionResolver<SerializableResolver<T>, T>(type),
    SlashOptionResolver<SerializableResolver<T>, T>,
    ComponentOptionResolver<SerializableResolver<T>, T>,
    ChoiceMapper<SerializableResolver<T>, T> {
    override val optionType = OptionType.STRING

    private fun encode(value: T) = json.encodeToString(serializer, value)

    private fun decode(encoded: String) = json.decodeFromString(serializer, encoded)

    override suspend fun toChoice(value: T): Command.Choice {
        val name = when (value) {
            is DordSerializable -> value.displayName()
            else -> value.toString()
        }

        return Command.Choice(name, encode(value))
    }

    override suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?) =
        optionMapping?.let { decode(it.asString) }

    override suspend fun resolve(handlerName: String, event: GenericComponentInteractionCreateEvent, arg: String) =
        StringResolver().resolve(handlerName, event, arg)?.let(::decode)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as SerializableResolver<*>

        if (serializer != other.serializer) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = serializer.hashCode()
        result = 31 * result + type.hashCode()
        return result
    }

    companion object {
        var json = Json
    }
}

interface DordSerializable {
    fun displayName(): String
}