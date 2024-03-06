package com.pattexpattex.dord

import com.pattexpattex.dord.commands.BaseCommandBuilder
import com.pattexpattex.dord.commands.CommandBuilder
import com.pattexpattex.dord.event.EventHandler
import com.pattexpattex.dord.event.EventHandlerBuilder
import com.pattexpattex.dord.event.EventPipeline
import dev.minn.jda.ktx.events.CoroutineEventListener
import dev.minn.jda.ktx.util.SLF4J
import kotlinx.atomicfu.atomic
import net.dv8tion.jda.api.JDA
import net.dv8tion.jda.api.events.GenericEvent
import net.dv8tion.jda.api.interactions.commands.build.CommandData
import net.dv8tion.jda.api.requests.RestAction
import java.util.concurrent.CopyOnWriteArrayList
import kotlin.reflect.full.createType
import kotlin.reflect.full.isSupertypeOf
import kotlin.time.measureTimedValue

@BuilderMarker
class Dord internal constructor() : CoroutineEventListener {
    private val log by SLF4J

    private val handlers = CopyOnWriteArrayList<EventHandler>()
    private val errorHandlers = CopyOnWriteArrayList<EventHandler>()
    private val returnHandlers = CopyOnWriteArrayList<EventHandler>()
    private val commands = CopyOnWriteArrayList<CommandData>()
    private var commandsForUpdate by atomic(false)

    override suspend fun onEvent(event: GenericEvent) {
        updateCommands(event.jda)

        val handler = handlers.find { it.eventType == event::class.createType() && it.doesFilter(event, Unit) } ?: return

        val result = measureTimedValue {
            handleEvent(handler, event)
        }

        log.debug("Handled {} in {}", event, result.duration)
    }

    private suspend fun handleEvent(
        handler: EventHandler,
        event: GenericEvent,
        arg: Any = Unit,
        isReturnHandler: Boolean = false,
    ): Result<Any?> {
        val pipeline = handler.createPipeline(event)

        return handler.call(pipeline, arg)
            .recoverCatching { throwable -> recoverFromException(pipeline, throwable) }
            .onFailure { log.error("Handler threw an exception", it) }
            .mapCatching {
                when {
                    !isReturnHandler && (it != null) && (it !is Unit) && (it !is RestAction<*>) -> handleReturnedValue(event, it)
                    it is RestAction<*> -> it.queue()
                    else -> it
                }
            }
    }

    private suspend fun recoverFromException(
        pipeline: EventPipeline<GenericEvent>,
        throwable: Throwable,
    ): Any? {
        val event = pipeline.event

        val filter: (EventHandler) -> Boolean = {
            it.eventType.isSupertypeOf(event::class.createType())
                    && it.argType.isSupertypeOf(throwable::class.createType())
                    && it.doesFilter(event, throwable)
        }

        val errorHandler = pipeline.errorHandlers.find(filter)
            ?: errorHandlers.find(filter)
            ?: throw throwable

        val errorPipeline = errorHandler.createPipeline(event)
        val result = errorHandler.call(errorPipeline, throwable)

        if (result.isFailure) {
            throw result.exceptionOrNull()!!.apply { addSuppressed(throwable) }
        }

        return result.getOrThrow()
    }

    private suspend fun handleReturnedValue(event: GenericEvent, value: Any): Any? {
        val handler = returnHandlers.find {
            it.eventType.isSupertypeOf(event::class.createType())
                    && it.argType.isSupertypeOf(value::class.createType())
                    && it.doesFilter(event, value)
        }

        if (handler == null) {
            return null
        }

        return handleEvent(handler, event, value, true).getOrThrow()
    }

    @BuilderMarker
    fun commands(builder: CommandBuilder.() -> Unit) {
        commands += CommandBuilder(this).apply(builder).commands.map(BaseCommandBuilder::build)
        commandsForUpdate = true
    }

    @BuilderMarker
    fun handlers(builder: EventHandlerBuilder.() -> Unit) {
        val ehBuilder = EventHandlerBuilder().apply(builder)
        putHandlers(handlers, ehBuilder.handlers)
        putHandlers(errorHandlers, ehBuilder.errorHandlers)
        putHandlers(returnHandlers, ehBuilder.returnHandlers)
    }

    private fun putHandlers(destination: MutableList<EventHandler>, source: List<EventHandler>) {
        val filtered = source.filterNot { handler ->
            destination.any { it.name == handler.name }.also { if (it) log.warn("Duplicate event handler \"${handler.name}\"") }
        }

        destination.addAll(filtered)
    }

    private fun updateCommands(jda: JDA) {
        if (commandsForUpdate && jda.status == JDA.Status.CONNECTED) {
            jda.updateCommands().addCommands(commands).queue()
            commandsForUpdate = false
        }
    }
}

@BuilderMarker
fun Dord(builder: Dord.() -> Unit): Dord {
    val value = measureTimedValue {
        Dord().apply(builder)
    }

    SLF4J<Dord>().value.info("Dord initialized in {}", value.duration)
    return value.value
}

typealias EventHandlerFunction<E, T> = suspend EventPipeline<E>.(T) -> Any?

typealias FilterFunction<E, T> = (E, T) -> Boolean

@Target(AnnotationTarget.CLASS, AnnotationTarget.TYPEALIAS, AnnotationTarget.FUNCTION, AnnotationTarget.PROPERTY)
@DslMarker
annotation class BuilderMarker