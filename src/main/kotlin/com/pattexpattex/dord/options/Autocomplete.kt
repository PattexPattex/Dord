package com.pattexpattex.dord.options

import net.dv8tion.jda.api.events.interaction.command.CommandAutoCompleteInteractionEvent

suspend inline fun <reified T : Any> CommandAutoCompleteInteractionEvent.replyChoices(choices: Collection<T>) =
    replyChoices(choices.map { Resolvers.toChoice(it) })

suspend inline fun <reified T : Any> CommandAutoCompleteInteractionEvent.replyChoices(vararg choices: T) =
    replyChoices(choices.map { Resolvers.toChoice(it) })

