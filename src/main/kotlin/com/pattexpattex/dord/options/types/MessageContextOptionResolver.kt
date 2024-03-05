package com.pattexpattex.dord.options.types

import com.pattexpattex.dord.options.OptionResolver
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent

interface MessageContextOptionResolver<T, R : Any> where T : OptionResolver<T, R>,
                                                         T : MessageContextOptionResolver<T, R> {
    suspend fun resolve(event: MessageContextInteractionEvent): R?
}