package com.pattexpattex.dord.commands

import com.pattexpattex.dord.BuilderMarker
import com.pattexpattex.dord.Dord

@BuilderMarker
class CommandBuilder(private val dord: Dord, internal val namePrefix: String = "") {
    internal val commands = mutableListOf<BaseCommandBuilder>()

    private fun compileName(name: String) = "$namePrefix $name".trim().ifEmpty {
        throw IllegalArgumentException("Command name cannot be empty")
    }

    @BuilderMarker
    fun prefix(prefix: String, builder: CommandBuilder.() -> Unit) {
        val subBuilder = CommandBuilder(dord, prefix).apply(builder)
        commands += subBuilder.commands
    }

    @BuilderMarker
    fun slash(name: String, description: String, builder: SlashCommandBuilder.() -> Unit = {}) {
        commands += SlashCommandBuilder(dord, compileName(name), description).apply(builder)
    }

    @BuilderMarker
    fun user(name: String, builder: BaseCommandBuilder.() -> Unit = {}) {
        commands += BaseCommandBuilder.user(dord, compileName(name)).apply(builder)
    }

    @BuilderMarker
    fun message(name: String, builder: BaseCommandBuilder.() -> Unit = {}) {
        commands += BaseCommandBuilder.message(dord, compileName(name)).apply(builder)
    }

    @BuilderMarker
    fun slash(description: String, builder: SlashCommandBuilder.() -> Unit = {}) = slash("", description, builder)

    @BuilderMarker
    fun user(builder: BaseCommandBuilder.() -> Unit = {}) = user("", builder)

    @BuilderMarker
    fun message(builder: BaseCommandBuilder.() -> Unit = {}) = message("", builder)
}