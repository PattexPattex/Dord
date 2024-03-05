package com.pattexpattex.dord.commands

import com.pattexpattex.dord.BuilderMarker
import net.dv8tion.jda.api.interactions.DiscordLocale

@BuilderMarker
sealed interface OptionsContainer {
    var options: MutableList<OptionBuilder>

    fun addOptions(options: Collection<OptionBuilder>) {
        this.options += options
    }
}

@BuilderMarker
sealed interface SubcommandsContainer {
    var subcommands: MutableList<SubcommandBuilder>

    @BuilderMarker
    fun subcommand(name: String, description: String, builder: SubcommandBuilder.() -> Unit = {}) {
        subcommands += SubcommandBuilder(name, description).apply(builder)
    }

    fun addSubcommands(subcommands: Collection<SubcommandBuilder>) {
        this.subcommands += subcommands
    }
}

@BuilderMarker
sealed interface SubcommandGroupsContainer {
    var subcommandGroups: MutableList<SubcommandGroupBuilder>

    @BuilderMarker
    fun group(name: String, description: String, builder: SubcommandGroupBuilder.() -> Unit) {
        subcommandGroups += SubcommandGroupBuilder(name, description).apply(builder)
    }
}

@BuilderMarker
sealed interface NameLocalizationsContainer {
    var nameLocalizations: MutableMap<DiscordLocale, String>
    var name: String
}

@BuilderMarker
sealed interface DescriptionLocalizationsContainer {
    var descriptionLocalizations: MutableMap<DiscordLocale, String>
    var description: String
}

sealed interface LocalizationsContainer : NameLocalizationsContainer, DescriptionLocalizationsContainer