package com.pattexpattex.dord.options.types

import com.pattexpattex.dord.options.OptionResolver
import net.dv8tion.jda.api.events.interaction.component.GenericComponentInteractionCreateEvent

interface ComponentOptionResolver<T, R : Any> where T : OptionResolver<T, R>, T : ComponentOptionResolver<T, R> {
    suspend fun resolve(handlerName: String, event: GenericComponentInteractionCreateEvent, arg: String): R?

    companion object {
        private val optionRegex = Regex("\\{([\\w_]+)}")

        private val handlerPatternOptionReplacement: (MatchResult) -> String = { "\\E(?<${it.groups[1]!!.value}>.+)\\Q" }

        fun handlerPatternToRegex(handlerName: String) = Regex(
            Regex.escape(handlerName).replace(optionRegex, handlerPatternOptionReplacement)
        )

        fun handlerPatternToRegexWithGroups(handlerName: String): Pair<Regex, List<String>> {
            val groups = mutableListOf<String>()
            val regex = Regex.escape(handlerName).replace(optionRegex) {
                groups += it.groups[1]!!.value
                handlerPatternOptionReplacement(it)
            }
            return Regex(regex) to groups.toList()
        }
    }
}