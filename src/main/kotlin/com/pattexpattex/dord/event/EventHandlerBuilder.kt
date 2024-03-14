package com.pattexpattex.dord.event

import com.pattexpattex.dord.BuilderMarker
import com.pattexpattex.dord.EventHandlerFunction
import com.pattexpattex.dord.FilterFunction
import com.pattexpattex.dord.options.types.ComponentOptionResolver
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.EntitySelectInteractionEvent
import net.dv8tion.jda.api.events.interaction.component.StringSelectInteractionEvent
import net.dv8tion.jda.api.interactions.components.ComponentInteraction
import kotlin.reflect.jvm.jvmName

@BuilderMarker
class EventHandlerBuilder(internal val namePrefix: String = "") {
    @PublishedApi internal val handlers = mutableListOf<EventHandler>()
    @PublishedApi internal val errorHandlers = mutableListOf<EventHandler>()
    @PublishedApi internal val returnHandlers = mutableListOf<EventHandler>()

    private fun compileName(name: String) = "$namePrefix $name".trim().ifEmpty {
        throw IllegalArgumentException("Command name cannot be empty")
    }

    @BuilderMarker
    fun prefix(prefix: String, builder: EventHandlerBuilder.() -> Unit) {
        val subBuilder = EventHandlerBuilder(compileName(prefix)).apply(builder)
        handlers += subBuilder.handlers
        errorHandlers += subBuilder.errorHandlers
        returnHandlers += subBuilder.returnHandlers
    }

    @BuilderMarker
    @JvmName("onGeneric")
    inline fun <reified T : Throwable> onError(
        noinline filter: FilterFunction<GenericEvent, T> = { _, _ -> true },
        noinline handler: EventHandlerFunction<GenericEvent, T>
    ) = onError<GenericEvent, _>(filter, handler)

    @BuilderMarker
    inline fun <reified E : GenericEvent, reified T : Throwable> onError(
        noinline filter: FilterFunction<E, T> = { _, _ -> true },
        noinline handler: EventHandlerFunction<E, T>
    ) {
        errorHandlers += genericOn(handler, filter)
    }

    @BuilderMarker
    @JvmName("onReturnedGeneric")
    inline fun <reified T : Any> onReturn(
        noinline filter: FilterFunction<GenericEvent, T> = { _, _ -> true },
        noinline handler: EventHandlerFunction<GenericEvent, T>
    ) = onReturn<GenericEvent, _>(filter, handler)

    @BuilderMarker
    inline fun <reified E : GenericEvent, reified T : Any> onReturn(
        noinline filter: FilterFunction<E, T> = { _, _ -> true },
        noinline handler: EventHandlerFunction<E, T>
    ) {
        returnHandlers += genericOn(handler, filter)
    }

    @BuilderMarker
    inline fun <reified E : GenericEvent> onEvent(
        crossinline filter: (E) -> Boolean = { true },
        noinline handler: EventHandlerFunction<E, Unit>
    ) {
        handlers += genericOn(handler) { event, _ -> filter(event) }
    }

    @BuilderMarker
    fun slash(
        name: String = "",
        filter: (SlashCommandInteractionEvent) -> Boolean = { true },
        handler: EventHandlerFunction<SlashCommandInteractionEvent, Unit>,
    ) {
        val prefixedName = compileName(name)

        handlers += baseBuilder(prefixedName, handler) { event, _ ->
            event.fullCommandName == prefixedName.replace('/', ' ') && filter(event)
        }
    }

    @BuilderMarker
    fun autocomplete(
        name: String = "",
        filter: (CommandAutoCompleteInteractionEvent) -> Boolean = { true },
        handler: EventHandlerFunction<CommandAutoCompleteInteractionEvent, Unit>
    ) {
        val prefixedName = compileName(name)

        handlers += baseBuilder(prefixedName, handler) { event, _ ->
            "${event.fullCommandName} ${event.focusedOption.name}" == prefixedName.replace('/', ' ') && filter(event)
        }
    }

    @BuilderMarker
    fun userContext(
        name: String = "",
        filter: (UserContextInteractionEvent) -> Boolean = { true },
        handler: EventHandlerFunction<UserContextInteractionEvent, Unit>
    ) {
        handlers += baseBuilder(compileName(name), handler) { event, _ -> filter(event) }
    }

    @BuilderMarker
    fun messageContext(
        name: String = "",
        filter: (MessageContextInteractionEvent) -> Boolean = { true },
        handler: EventHandlerFunction<MessageContextInteractionEvent, Unit>
    ) {
        handlers += baseBuilder(compileName(name), handler) { event, _ -> filter(event) }
    }

    @BuilderMarker
    fun modal(
        name: String = "",
        filter: (ModalInteractionEvent) -> Boolean = { true },
        handler: EventHandlerFunction<ModalInteractionEvent, Unit>
    ) {
        val prefixedName = compileName(name)

        handlers += baseBuilder(prefixedName, handler) { it, _ ->
            ComponentOptionResolver.handlerPatternToRegex(prefixedName).matches(it.modalId) && filter(it)
        }
    }

    @BuilderMarker
    fun button(
        name: String = "",
        filter: (ButtonInteractionEvent) -> Boolean = { true },
        handler: EventHandlerFunction<ButtonInteractionEvent, Unit>,
    ) = componentHandler<ButtonInteractionEvent>(name, filter, handler)

    @BuilderMarker
    fun entitySelectMenu(
        name: String = "",
        filter: (EntitySelectInteractionEvent) -> Boolean = { true },
        handler: EventHandlerFunction<EntitySelectInteractionEvent, Unit>
    ) = componentHandler<EntitySelectInteractionEvent>(name, filter, handler)

    @BuilderMarker
    fun stringSelectMenu(
        name: String = "",
        filter: (StringSelectInteractionEvent) -> Boolean = { true },
        handler: EventHandlerFunction<StringSelectInteractionEvent, Unit>
    ) = componentHandler<StringSelectInteractionEvent>(name, filter, handler)

    private inline fun <reified T> componentHandler(
        name: String,
        crossinline filter: (T) -> Boolean,
        noinline handler: EventHandlerFunction<T, Unit>,
    ) where T : GenericEvent, T : ComponentInteraction {
        val prefixedName = compileName(name)

        handlers += baseBuilder(prefixedName, handler) { it, _ ->
            ComponentOptionResolver.handlerPatternToRegex(prefixedName).matches(it.componentId) && filter(it)
        }
    }

    @PublishedApi
    internal inline fun <reified E : GenericEvent, reified T> genericOn(
        noinline handler: EventHandlerFunction<E, T>,
        noinline filter: FilterFunction<E, T> = { _, _ -> true },
    ) = baseBuilder(handler::class.jvmName, handler, filter)

    @PublishedApi
    internal inline fun <reified E : GenericEvent, reified T> baseBuilder(
        name: String,
        noinline handler: EventHandlerFunction<E, T>,
        noinline filter: FilterFunction<E, T> = { _, _ -> true },
    ) = EventHandler.create(name, handler, filter)
}