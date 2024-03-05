package com.pattexpattex.dord.options.types

import com.pattexpattex.dord.options.OptionResolver
import net.dv8tion.jda.api.events.interaction.ModalInteractionEvent
import net.dv8tion.jda.api.interactions.modals.ModalMapping

interface ModalOptionResolver<T, R : Any> where T : OptionResolver<T, R>, T : ModalOptionResolver<T, R> {
    suspend fun resolve(event: ModalInteractionEvent, mapping: ModalMapping?): R?
}