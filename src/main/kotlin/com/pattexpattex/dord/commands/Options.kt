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

@BuilderMarker
inline fun <reified T> OptionsContainer.option(
    name: String,
    description: String,
    isAutocomplete: Boolean,
    builder: OptionBuilder.() -> Unit = {},
) {
    options += OptionBuilder<T>(dord, name, description, isAutocomplete).apply(builder)
}

@BuilderMarker
inline fun <reified T> OptionsContainer.option(
    name: String,
    description: String,
    noinline autocomplete: EventHandlerFunction<CommandAutoCompleteInteractionEvent, Unit>? = null,
    builder: OptionBuilder.() -> Unit = {},
) {
    registerAutocomplete(this, autocomplete)
    option<T>(name, description, autocomplete != null, builder)
}

@BuilderMarker
inline fun <reified T : Enum<T>> OptionsContainer.enumOption(
    name: String,
    description: String,
    isRequired: Boolean = true,
    values: Collection<T> = enumValues<T>().toList(),
    crossinline builder: OptionBuilder.() -> Unit = {}
) {
    Resolvers.register(Resolvers.enumResolver<T>(values))

    val actualBuilder: OptionBuilder.() -> Unit = {
        builder()
        choices += values.map { ChoiceBuilder(dord, it.name, it.name.lowercase()) }
    }

    if (isRequired) {
        option<T>(name, description, builder = actualBuilder)
    } else {
        option<T?>(name, description, builder = actualBuilder)
    }
}

@BuilderMarker
inline fun <reified T : GuildChannel?> OptionsContainer.channelOption(
    name: String,
    description: String,
    builder: OptionBuilder.() -> Unit = {},
) {
    option<T>(name, description) {
        builder()
        channelTypes += ChannelType.guildTypes().filter { T::class.isSuperclassOf(it.`interface`.kotlin) }
    }
}

@PublishedApi
internal fun registerAutocomplete(
    container: OptionsContainer,
    autocomplete: EventHandlerFunction<CommandAutoCompleteInteractionEvent, Unit>?
) {
    if (autocomplete != null) {
        val parentName = "${container.parentName} ${container.name}".trim()
        container.dord.handlers {
            prefix(parentName) {
                autocomplete(container.name, handler = autocomplete)
            }
        }
    }
}