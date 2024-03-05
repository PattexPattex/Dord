package com.pattexpattex.dord.options

import com.pattexpattex.dord.event.EventPipeline
import com.pattexpattex.dord.options.impl.*
import com.pattexpattex.dord.options.types.*
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionType
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

object Resolvers {
    @PublishedApi
    internal val resolvers = listOf<OptionResolver<*, *>>(
        AttachmentResolver(),
        BooleanResolver(),
        DoubleResolver(),
        GuildChannelResolver(),
        GuildResolver(),
        IMentionableResolver(),
        InputUserResolver(),
        IntResolver(),
        LongResolver(),
        MemberResolver(),
        MessageResolver(),
        RoleResolver(),
        StringResolver(),
        UserResolver()
    ).toCollection(CopyOnWriteArraySet())

    fun register(resolver: OptionResolver<*, *>) {
        resolvers.add(resolver)
    }

    inline fun <reified E : Enum<E>> enumResolver(
        values: Collection<E> = enumValues<E>().toList()
    ): OptionResolver<EnumResolver<E>, E> = EnumResolver(typeOf<E>(), values.toCollection(EnumSet.noneOf(E::class.java)))

    @PublishedApi
    internal suspend inline fun <reified T : Any?, E : GenericEvent> resolve(
        pipeline: EventPipeline<E>,
        arg: String,
    ): T {
        val resolvedValue = resolvers.filter { typeOf<T>().withNullability(false).isSubtypeOf(it.resolvedType) }
            .firstNotNullOfOrNull {
                when (pipeline.event) {
                    is GenericComponentInteractionCreateEvent -> (it as ComponentOptionResolver<*, *>).resolve(pipeline.handler.name, pipeline.event, arg)
                    is MessageContextInteractionEvent -> (it as MessageContextOptionResolver<*, *>).resolve(pipeline.event)
                    is UserContextInteractionEvent -> (it as UserContextOptionResolver<*, *>).resolve(pipeline.event)
                    is ModalInteractionEvent -> (it as ModalOptionResolver<*, *>).resolve(pipeline.event, pipeline.event.getValue(arg))
                    is CommandInteractionPayload -> (it as SlashOptionResolver<*, *>).resolve(pipeline.event, pipeline.event.getOption(arg))
                    else -> (it as? GenericOptionResolver<*, *>)?.resolve(pipeline, arg)
                }
            }

        if (null is T && resolvedValue !is T) {
            return null as T
        }

        if (resolvedValue == null && null !is T) {
            throw NullPointerException("Resolved value is null")
        }

        if (resolvedValue !is T) {
            throw IllegalStateException("Resolved value \"$resolvedValue\" is not of type ${typeOf<T>()}")
        }

        return resolvedValue
    }

    @PublishedApi
    internal inline fun <reified T> SlashOptionResolver.Companion.mapTypeToOptionType(): OptionType? {
        val resolver: OptionResolver<*, *>? = resolvers
            .filter { it is SlashOptionResolver<*, *> }
            .firstOrNull { typeOf<T>().isSubtypeOf(it.resolvedType) }

        return (resolver as SlashOptionResolver<*, *>?)?.optionType
    }
}
