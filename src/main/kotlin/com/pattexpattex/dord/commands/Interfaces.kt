package com.pattexpattex.dord.commands

import com.pattexpattex.dord.BuilderMarker
import com.pattexpattex.dord.Dord
import net.dv8tion.jda.api.interactions.DiscordLocale

sealed interface DordContainer {
    val dord: Dord
}

@BuilderMarker
sealed interface OptionsContainer : DordContainer {
    var options: MutableList<OptionBuilder>
    val name: String
    val parentName: String

    fun addOptions(options: Collection<OptionBuilder>) {
        this.options += options
    }
}

@BuilderMarker
sealed interface SubcommandsContainer : DordContainer {
    var subcommands: MutableList<SubcommandBuilder>
    val name: String
    val parentName: String

    @BuilderMarker
    fun subcommand(name: String, description: String, builder: SubcommandBuilder.() -> Unit = {}) {
        subcommands += SubcommandBuilder(dord, "$parentName ${this.name}".trim(), name, description).apply(builder)
    }

    fun addSubcommands(subcommands: Collection<SubcommandBuilder>) {
        this.subcommands += subcommands
    }
}

@BuilderMarker
sealed interface SubcommandGroupsContainer : DordContainer {
    var subcommandGroups: MutableList<SubcommandGroupBuilder>
    val name: String
    val parentName: String

    @BuilderMarker
    fun group(name: String, description: String, builder: SubcommandGroupBuilder.() -> Unit) {
        subcommandGroups += SubcommandGroupBuilder(dord, "$parentName ${this.name}".trim(), name, description).apply(builder)
    }
}

@BuilderMarker
sealed interface NameLocalizationsContainer : DordContainer {
    var nameLocalizations: MutableMap<DiscordLocale, String>
    var name: String
}

@BuilderMarker
sealed interface DescriptionLocalizationsContainer : DordContainer {
    var descriptionLocalizations: MutableMap<DiscordLocale, String>
    var description: String
}

sealed interface LocalizationsContainer : NameLocalizationsContainer, DescriptionLocalizationsContainer