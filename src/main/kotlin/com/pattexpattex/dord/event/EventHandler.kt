package com.pattexpattex.dord.event

import com.pattexpattex.dord.EventHandlerFunction
import net.dv8tion.jda.api.events.GenericEvent
import kotlin.reflect.KType
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSupertypeOf
import kotlin.reflect.typeOf

class EventHandler @PublishedApi internal constructor(
    val name: String,
    val eventType: KType,
    val argType: KType,
    private val function: EventHandlerFunction<GenericEvent, Any>,
    private val filter: (GenericEvent, Any) -> Boolean
) {
    internal fun createPipeline(event: GenericEvent) = EventPipeline(cast(event), this)

    internal fun doesFilter(event: GenericEvent, arg: Any) = filter.invoke(event, arg)

    internal suspend fun call(pipeline: EventPipeline<GenericEvent>, arg: Any) = runCatching {
        function.invoke(pipeline, arg)
    }

    private fun cast(event: GenericEvent): GenericEvent {
        if (!eventType.isSupertypeOf(event::class.createType())) {
            throw IllegalArgumentException("$event is not instance of $eventType")
        }

        return event
    }

    @Suppress("UNCHECKED_CAST")
    companion object {
        @PublishedApi
        internal inline fun <reified E : GenericEvent, reified T> create(
            name: String,
            noinline function: EventHandlerFunction<E, T>,
            noinline filter: (E, T) -> Boolean,
        ) = EventHandler(
            name,
            typeOf<E>(),
            typeOf<T>(),
            function as EventHandlerFunction<GenericEvent, Any>,
            filter as (GenericEvent, Any) -> Boolean
        )

        @PublishedApi
        internal fun <E : GenericEvent, T> create(
            name: String,
            eventType: KType,
            argType: KType,
            function: EventHandlerFunction<out E, T>,
            filter: (E, T) -> Boolean,
        ) = EventHandler(
            name,
            eventType,
            argType,
            function as EventHandlerFunction<GenericEvent, Any>,
            filter as (GenericEvent, Any) -> Boolean
        )
    }
}
