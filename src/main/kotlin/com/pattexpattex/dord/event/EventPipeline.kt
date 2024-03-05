package com.pattexpattex.dord.event

import com.pattexpattex.dord.BuilderMarker
import com.pattexpattex.dord.EventHandlerFunction
import com.pattexpattex.dord.FilterFunction
import com.pattexpattex.dord.options.Resolvers
import net.dv8tion.jda.api.events.GenericEvent
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.full.createType
import kotlin.reflect.jvm.jvmName
import kotlin.reflect.typeOf

@BuilderMarker
class EventPipeline<out E : GenericEvent>(
    val event: E,
    val handler: EventHandler,
) {
    suspend inline fun <reified T : Any?> option(name: String) = Resolvers.resolve<T, E>(this, name)

    @PublishedApi
    internal val errorHandlers = CopyOnWriteArrayList<EventHandler>()

    inline fun <reified T : Throwable> onError(
        noinline filter: FilterFunction<E, T> = { _, _ -> true },
        noinline handler: EventHandlerFunction<E, T>
    ) {
        errorHandlers.add(EventHandler.create(handler::class.jvmName, event::class.createType(), typeOf<T>(), handler, filter))
    }

    inline fun <reified E> EventPipeline<GenericEvent>.eventAs() = event as? E

    @JvmName("eventAsUnit")
    suspend inline fun <reified E> EventPipeline<GenericEvent>.eventAs(crossinline block: suspend (E) -> Unit) {
        eventAs<E, Unit>(block)
    }

    suspend inline fun <reified E, T> EventPipeline<GenericEvent>.eventAs(crossinline block: suspend (E) -> T): T? =
        eventAs<E>()?.let { block(it) }
}
