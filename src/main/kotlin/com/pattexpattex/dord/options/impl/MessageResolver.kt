package com.pattexpattex.dord.options.impl

import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.types.MessageContextOptionResolver
import net.dv8tion.jda.api.entities.Message
import net.dv8tion.jda.api.events.interaction.command.MessageContextInteractionEvent
import kotlin.reflect.typeOf

class MessageResolver :
    OptionResolver<MessageResolver, Message>(typeOf<Message>()),
    MessageContextOptionResolver<MessageResolver, Message> {
    override suspend fun resolve(event: MessageContextInteractionEvent) = event.target
}