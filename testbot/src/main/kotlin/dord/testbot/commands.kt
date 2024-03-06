package dord.testbot

import com.pattexpattex.dord.Dord
import com.pattexpattex.dord.commands.option
import net.dv8tion.jda.api.interactions.commands.Command

fun Dord.setupNewCommands() = commands {
    prefix("foo") {
        slash("far") {
            option<Int>("bornana", "bleh", autocomplete = {
                event.replyChoices((1..5).map { Command.Choice(it.toString(), it.toLong()) })
            })
            option<String>("bleh", "bleh", true)
        }
    }
}