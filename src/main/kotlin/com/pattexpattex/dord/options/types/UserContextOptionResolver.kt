package com.pattexpattex.dord.options.types

import com.pattexpattex.dord.options.OptionResolver
import net.dv8tion.jda.api.events.interaction.command.UserContextInteractionEvent

interface UserContextOptionResolver<T, R : Any> where T : OptionResolver<T, R>, T : UserContextOptionResolver<T, R> {
    suspend fun resolve(event: UserContextInteractionEvent): R?
}