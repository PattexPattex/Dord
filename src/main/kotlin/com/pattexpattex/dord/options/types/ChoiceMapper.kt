package com.pattexpattex.dord.options.types

import com.pattexpattex.dord.options.OptionResolver
import net.dv8tion.jda.api.interactions.commands.Command

interface ChoiceMapper<T, R : Any> where T : OptionResolver<T, R>, T : ChoiceMapper<T, R> {
    suspend fun toChoice(value: R): Command.Choice
}