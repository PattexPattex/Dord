package dord.testbot

import com.pattexpattex.dord.Dord
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.commands.Command

fun Dord.setupHandlers() = handlers {
    prefix("foo stink") {
        slash {
            event.reply("${option<Int>("bornana")} bornana ${option<String?>("bleh")}")
        }

        autocomplete("bleh") {
            event.replyChoices((1..5).map { Command.Choice(it.toString(), it.toString()) })
        }

        onReturn<GenericInteractionCreateEvent, String> {
            eventAs<IReplyCallback> {
                it.reply("salam")
            }
        }
    }
}
