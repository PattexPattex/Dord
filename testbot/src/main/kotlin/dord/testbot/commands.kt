package dord.testbot

import com.pattexpattex.dord.Dord
import com.pattexpattex.dord.commands.option
import com.pattexpattex.dord.commands.serializableOption
import net.dv8tion.jda.api.interactions.commands.Command

fun Dord.setupNewCommands() = commands {
    slash("foo", "far") {
        subcommand("stink", "something stinks") {
            option<Int>("bornana", "bleh", autocomplete = {
                event.replyChoices((1..5).map { Command.Choice(it.toString(), it.toLong()) })
            })
            option<String?>("bleh", "bleh", isAutocomplete = false) {
                choices("null", "ono", "asda")
            }
        }
    }

    slash("resovertest", "yeaaaa") {
        serializableOption<CustomResolvedThing>("thing", "adad") {
            choices(CustomResolvedThing(Foo.Yes, false), CustomResolvedThing(Foo.No, true))
        }
    }
}