package com.pattexpattex.dord.options.impl

import com.pattexpattex.dord.options.OptionResolver
import com.pattexpattex.dord.options.types.SlashOptionResolver
import net.dv8tion.jda.api.entities.Message.Attachment
import net.dv8tion.jda.api.interactions.commands.CommandInteractionPayload
import net.dv8tion.jda.api.interactions.commands.OptionMapping
import net.dv8tion.jda.api.interactions.commands.OptionType
import kotlin.reflect.typeOf

class AttachmentResolver :
    OptionResolver<AttachmentResolver, Attachment>(typeOf<Attachment>()),
    SlashOptionResolver<AttachmentResolver, Attachment> {
    override val optionType = OptionType.ATTACHMENT

    override suspend fun resolve(event: CommandInteractionPayload, optionMapping: OptionMapping?) =
        optionMapping?.asAttachment
}