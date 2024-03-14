package dord.testbot

import com.pattexpattex.dord.Dord
import dev.minn.jda.ktx.interactions.components.Modal
import net.dv8tion.jda.api.events.interaction.GenericInteractionCreateEvent
import net.dv8tion.jda.api.interactions.callbacks.IReplyCallback
import net.dv8tion.jda.api.interactions.commands.Command

fun Dord.setupHandlers() = handlers {
    prefix("foo stink") {
        slash {
            val bleh = option<String?>("bleh")
            event.replyModal(Modal("modal", "bleeeee") {
                short("bah", bleh ?: "idk")
            })
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

    modal("modal") {
        event.reply(option<String>("bah"))
    }

    button("dord.resovertest.{thing}") {
        val thing = option<CustomResolvedThing>("thing")
        event.reply("${thing.foo} was ${thing.bornana} again!")
    }
}
