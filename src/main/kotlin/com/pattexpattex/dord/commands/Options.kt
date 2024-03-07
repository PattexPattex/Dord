package com.pattexpattex.dord.commands

import com.pattexpattex.dord.BuilderMarker
import com.pattexpattex.dord.EventHandlerFunction
import com.pattexpattex.dord.options.Resolvers
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.entities.channel.middleman.GuildChannel
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import kotlin.reflect.full.isSuperclassOf
import kotlin.reflect.typeOf

@BuilderMarker
inline fun <reified T> OptionsContainer.option(
    name: String,
    description: String,
    isAutocomplete: Boolean,
    isRequired: Boolean = !typeOf<T>().isMarkedNullable,
    builder: OptionBuilder<T>.() -> Unit = {},
) {
    options += OptionBuilder<T>(dord, name, description, isAutocomplete, isRequired).apply(builder)
}

@BuilderMarker
inline fun <reified T> OptionsContainer.option(
    name: String,
    description: String,
    isRequired: Boolean = !typeOf<T>().isMarkedNullable,
    noinline autocomplete: EventHandlerFunction<CommandAutoCompleteInteractionEvent, Unit>? = null,
    builder: OptionBuilder<T>.() -> Unit = {},
) {
    if (autocomplete != null) {
        dord.handlers {
            prefix("${this@option.parentName} ${this@option.name}".trim()) {
                autocomplete(name, handler = autocomplete)
            }
        }
    }

    option<T>(name, description, autocomplete != null, isRequired, builder)
}

@BuilderMarker
inline fun <reified T : Enum<T>> OptionsContainer.enumOption(
    name: String,
    description: String,
    isRequired: Boolean = true,
    values: Collection<T> = enumValues<T>().toList(),
    crossinline builder: OptionBuilder<T>.() -> Unit = {}
) {
    Resolvers.register(Resolvers.enumResolver<T>(values))

    option<T>(name, description, isRequired = isRequired) {
        builder()
        choices += values.map { ChoiceBuilder(dord, it.name, it.name.lowercase()) }
    }
}

@BuilderMarker
inline fun <reified T : GuildChannel?> OptionsContainer.channelOption(
    name: String,
    description: String,
    builder: OptionBuilder<T>.() -> Unit = {},
) {
    option<T>(name, description) {
        builder()
        channelTypes += ChannelType.guildTypes().filter { T::class.isSuperclassOf(it.`interface`.kotlin) }
    }
}
