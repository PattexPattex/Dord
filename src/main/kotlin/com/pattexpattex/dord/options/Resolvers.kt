package com.pattexpattex.dord.options

import com.pattexpattex.dord.event.EventPipeline
import com.pattexpattex.dord.options.impl.*
import com.pattexpattex.dord.options.types.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.serializer
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import java.util.*
import java.util.concurrent.CopyOnWriteArraySet
import kotlin.reflect.KType
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

    fun register(resolvers: Collection<OptionResolver<*, *>>) {
        this.resolvers.addAll(resolvers)
    }

    inline fun <reified T : Enum<T>> enumResolver(values: Collection<T> = enumValues<T>().toList()): EnumResolver<T> {
        return EnumResolver(typeOf<T>(), values.toCollection(EnumSet.noneOf(T::class.java)))
    }

    inline fun <reified T : Any> serializableResolver(serializer: KSerializer<T> = serializer()): SerializableResolver<T> {
        return SerializableResolver(serializer, typeOf<T>())
    }

    inline fun <reified T> isRegistered(): Boolean {
        return resolvers.any { typeOf<T>().withNullability(false).isSubtypeOf(it.resolvedType) }
    }

    suspend inline fun <reified T : Any> serialize(value: T) = toChoice(value, typeOf<T>()).asString

    suspend inline fun <reified T : Any> toChoice(value: T) = toChoice(value, typeOf<T>())

    @PublishedApi
    internal suspend fun <T : Any> toChoice(value: T, type: KType): Command.Choice {
        val mapper = resolvers
            .filter { type.withNullability(false).isSubtypeOf(it.resolvedType) }
            .filterIsInstance<ChoiceMapper<*, T>>()
            .firstOrNull()

        if (mapper == null) {
            throw IllegalArgumentException("Choice mapper for type $type not found")
        }

        return mapper.toChoice(value)
    }

    @PublishedApi
    internal suspend inline fun <reified T : Any?, E : GenericEvent> resolve(
        pipeline: EventPipeline<E>,
        arg: String,
    ): T {
        val type = typeOf<T>()
        val result = runCatching {
            resolvers.filter { type.withNullability(false).isSubtypeOf(it.resolvedType) }
                .firstNotNullOfOrNull {
                    when (pipeline.event) {
                        is GenericComponentInteractionCreateEvent -> (it as? ComponentOptionResolver<*, *>)?.resolve(
                            pipeline.handler.name,
                            pipeline.event,
                            arg
                        )

                        is MessageContextInteractionEvent -> (it as? MessageContextOptionResolver<*, *>)?.resolve(pipeline.event)

                        is UserContextInteractionEvent -> (it as? UserContextOptionResolver<*, *>)?.resolve(pipeline.event)

                        is ModalInteractionEvent -> (it as? ModalOptionResolver<*, *>)?.resolve(
                            pipeline.event,
                            pipeline.event.getValue(arg)
                        )

                        is CommandInteractionPayload -> (it as? SlashOptionResolver<*, *>)?.resolve(
                            pipeline.event,
                            pipeline.event.getOption(arg)
                        )

                        else -> (it as? GenericOptionResolver<*, *>)?.resolve(pipeline, arg)
                    } 
                }
        }

        if (result.isFailure) {
            throw DordResolverException("Resolver for \"$arg\": $type threw an exception", arg, type, result.exceptionOrNull()!!)
        }

        val resolvedValue = result.getOrNull()

        if (null is T && resolvedValue !is T) {
            return null as T
        }

        if (resolvedValue == null && null !is T) {
            throw DordResolvedValueException("Resolved value \"$arg\" is null", arg, type)
        }

        if (resolvedValue !is T) {
            throw DordResolvedValueException("Resolved value \"$resolvedValue\" (named $arg) is not of type $type", arg, type)
        }

        return resolvedValue
    }
}
