package com.pattexpattex.dord.options.types

import com.pattexpattex.dord.event.EventPipeline
import com.pattexpattex.dord.options.OptionResolver
import net.dv8tion.jda.api.events.GenericEvent

interface GenericOptionResolver<T, R : Any> where T : OptionResolver<T, R>, T : GenericOptionResolver<T, R> {
    suspend fun resolve(pipeline: EventPipeline<GenericEvent>, arg: String): R?
}