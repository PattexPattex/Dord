package com.pattexpattex.dord.options.impl

import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.types.SlashOptionResolver
import net.dv8tion.jda.api.entities.IMentionable
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.typeOf

class IMentionableResolver :
    OptionResolver<IMentionableResolver, IMentionable>(typeOf<IMentionable>()),
    SlashOptionResolver<IMentionableResolver, IMentionable> {
    override val optionType = OptionType.MENTIONABLE

    override suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?) =
        optionMapping?.asMentionable
}