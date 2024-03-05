package dord.testbot

import com.pattexpattex.dord.Dord
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.commands.Command

fun Dord.setupHandlers() = handlers {
    prefix("foo") {
        slash {
            event.reply("${option<Int>("bornana")} bornana")
        }

        autocomplete("bornana") {
            event.replyChoices((1..5).map { Command.Choice(it.toString(), it.toLong()) })
        }

        onReturn<GenericInteractionCreateEvent, String> {
            eventAs<IReplyCallback> {
                it.reply("salam")
            }
        }
    }
}
