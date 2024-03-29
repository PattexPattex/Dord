package com.pattexpattex.dord.commands

import com.pattexpattex.dord.BuilderMarker
import com.pattexpattex.dord.Dord
import com.pattexpattex.dord.options.Resolvers
import com.pattexpattex.dord.options.types.SlashOptionResolver
import dev.minn.jda.ktx.interactions.commands.Subcommand
import dev.minn.jda.ktx.interactions.commands.SubcommandGroup
import kotlinx.coroutines.runBlocking
import net.dv8tion.jda.api.entities.channel.ChannelType
import net.dv8tion.jda.api.interactions.DiscordLocale
import net.dv8tion.jda.api.interactions.commands.Command
import net.dv8tion.jda.api.interactions.commands.Command.Choice
import net.dv8tion.jda.api.interactions.commands.DefaultMemberPermissions
import net.dv8tion.jda.api.interactions.commands.OptionType
import net.dv8tion.jda.api.interactions.commands.build.Commands
import net.dv8tion.jda.api.interactions.commands.build.OptionData
import net.dv8tion.jda.api.interactions.commands.localization.LocalizationFunction
import java.util.*
import kotlin.reflect.KType
import kotlin.reflect.full.withNullability
import kotlin.reflect.typeOf

@BuilderMarker
open class BaseCommandBuilder(
    override val dord: Dord,
    override var name: String,
    val type: Command.Type,
) : NameLocalizationsContainer {
    override var nameLocalizations = mutableMapOf<DiscordLocale, String>()
    var localizationFunction = LocalizationFunction { mapOf() }
    var defaultPermissions = DefaultMemberPermissions.ENABLED
    var isGuildOnly = false
    var isNSFW = false

    internal open fun build() = Commands.context(type, name)
        .setLocalizationFunction(localizationFunction)
        .setNameLocalizations(nameLocalizations)
        .setDefaultPermissions(defaultPermissions)
        .setGuildOnly(isGuildOnly)
        .setNSFW(isNSFW)

    companion object {
        fun message(dord: Dord, name: String) = BaseCommandBuilder(dord, name, Command.Type.MESSAGE)

        fun user(dord: Dord, name: String) = BaseCommandBuilder(dord, name, Command.Type.USER)
    }
}

@BuilderMarker
class SlashCommandBuilder(
    dord: Dord,
    name: String,
    override var description: String,
) :
    BaseCommandBuilder(dord, name, Command.Type.SLASH),
    OptionsContainer,
    SubcommandsContainer,
    SubcommandGroupsContainer,
    DescriptionLocalizationsContainer
{
    override var options = mutableListOf<OptionBuilder<*>>()
    override var descriptionLocalizations = mutableMapOf<DiscordLocale, String>()
    override var subcommands = mutableListOf<SubcommandBuilder>()
    override var subcommandGroups = mutableListOf<SubcommandGroupBuilder>()
    override val parentName = ""

    override fun build() = Commands.slash(name, description)
        .setDescriptionLocalizations(descriptionLocalizations)
        .addOptions(options.map(OptionBuilder<*>::build))
        .addSubcommands(subcommands.map(SubcommandBuilder::build))
        .addSubcommandGroups(subcommandGroups.map(SubcommandGroupBuilder::build))
        .setLocalizationFunction(localizationFunction)
        .setNameLocalizations(nameLocalizations)
        .setDefaultPermissions(defaultPermissions)
        .setGuildOnly(isGuildOnly)
        .setNSFW(isNSFW)
}

@BuilderMarker
class SubcommandBuilder(
    override val dord: Dord,
    override val parentName: String,
    override var name: String,
    override var description: String
) : OptionsContainer, LocalizationsContainer {
    override var options = mutableListOf<OptionBuilder<*>>()
    override var nameLocalizations = mutableMapOf<DiscordLocale, String>()
    override var descriptionLocalizations = mutableMapOf<DiscordLocale, String>()

    internal fun build() = Subcommand(name, description)
        .setNameLocalizations(nameLocalizations)
        .setDescriptionLocalizations(descriptionLocalizations)
        .addOptions(options.map(OptionBuilder<*>::build))
}

@BuilderMarker
class SubcommandGroupBuilder(
    override val dord: Dord,
    override val parentName: String,
    override var name: String,
    override var description: String,
) : SubcommandsContainer, LocalizationsContainer {
    override var nameLocalizations = mutableMapOf<DiscordLocale, String>()
    override var descriptionLocalizations = mutableMapOf<DiscordLocale, String>()
    override var subcommands = mutableListOf<SubcommandBuilder>()

    internal fun build() = SubcommandGroup(name, description)
        .setNameLocalizations(nameLocalizations)
        .setDescriptionLocalizations(descriptionLocalizations)
        .addSubcommands(subcommands.map(SubcommandBuilder::build))
}

inline fun <reified T> OptionBuilder(
    dord: Dord,
    name: String,
    description: String,
    isAutocomplete: Boolean,
    isRequired: Boolean
): OptionBuilder<T> {
    val optionType = SlashOptionResolver.toOptionType<T>()
        ?: throw IllegalArgumentException("Cannot map \"${typeOf<T>()}\" to OptionType, please register a resolver")

    return OptionBuilder(typeOf<T>().withNullability(false), dord, name, description, isAutocomplete, isRequired, optionType)
}

@BuilderMarker
open class OptionBuilder<T> @PublishedApi internal constructor(
    private val type: KType,
    override val dord: Dord,
    override var name: String,
    override var description: String,
    var isAutocomplete: Boolean,
    var isRequired: Boolean,
    val optionType: OptionType
) : LocalizationsContainer {
    override var nameLocalizations = mutableMapOf<DiscordLocale, String>()
    override var descriptionLocalizations = mutableMapOf<DiscordLocale, String>()
    var channelTypes = EnumSet.noneOf(ChannelType::class.java)
    var minValue: Number = OptionData.MIN_NEGATIVE_NUMBER
    var maxValue: Number = OptionData.MAX_POSITIVE_NUMBER
    var minLength: Int = 1
    var maxLength: Int = OptionData.MAX_STRING_OPTION_LENGTH
    var choices = mutableListOf<ChoiceBuilder>()

    fun setValueRange(minValue: Number = OptionData.MIN_NEGATIVE_NUMBER, maxValue: Number = OptionData.MAX_POSITIVE_NUMBER) {
        this.minValue = minValue
        this.maxValue = maxValue
    }

    fun setLengthRange(minLength: Int = 0, maxLength: Int = OptionData.MAX_STRING_OPTION_LENGTH) {
        this.minLength = minLength
        this.maxLength = maxLength
    }

    @BuilderMarker
    fun choices(choices: Collection<Choice>) {
        this.choices += choices.map { ChoiceBuilder(dord, it.name, it.toData(optionType).get("value")) }
    }

    @BuilderMarker
    fun choices(vararg choices: Choice) {
        choices(choices.toList())
    }

    @BuilderMarker
    fun choice(choice: Choice, builder: ChoiceBuilder.() -> Unit) {
        choices += ChoiceBuilder(dord, choice.name, choice.toData(optionType).get("value")).apply(builder)
    }

    @JvmName("choicesTyped")
    @BuilderMarker
    fun choices(choices: Collection<T>) {
        runBlocking {
            choices(choices.map { Resolvers.toChoice(requireNotNull(it), type) })
        }
    }

    @BuilderMarker
    fun choices(vararg choices: T) {
        choices(choices.toList())
    }

    @BuilderMarker
    fun choice(value: T, builder: ChoiceBuilder.() -> Unit = {}) {
        runBlocking {
            choice(Resolvers.toChoice(requireNotNull(value), type), builder)
        }
    }

    internal fun build() = OptionData(optionType, name, description)
        .setNameLocalizations(nameLocalizations)
        .setDescriptionLocalizations(descriptionLocalizations)
        .setAutoComplete(isAutocomplete)
        .setRequired(isRequired)
        .also {
            if (optionType in EnumSet.of(OptionType.NUMBER, OptionType.STRING, OptionType.INTEGER)) {
                it.addChoices(choices.map(ChoiceBuilder::build))
            }

            when (optionType) {
                OptionType.CHANNEL -> it.setChannelTypes(channelTypes)
                OptionType.NUMBER -> it.setRequiredRange(minValue.toDouble(), maxValue.toDouble())
                OptionType.STRING -> it.setRequiredLength(minLength, maxLength)
                else -> {}
            }
        }
}

@BuilderMarker
class ChoiceBuilder @PublishedApi internal constructor(
    override val dord: Dord,
    override var name: String,
    var value: Any,
) : NameLocalizationsContainer {
    override var nameLocalizations = mutableMapOf<DiscordLocale, String>()

    internal fun build() = when (val value = value) {
        is String -> Choice(name, value)
        is Long -> Choice(name, value)
        is Double -> Choice(name, value)
        else -> Choice(name, value.toString())
    }.setNameLocalizations(nameLocalizations)
}
